package ru.skillbranch.skillarticles.viewmodels.article

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.skillbranch.skillarticles.data.models.ArticleData
import ru.skillbranch.skillarticles.data.models.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.models.CommentItemData
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.data.repositories.CommentsDataFactory
import ru.skillbranch.skillarticles.data.repositories.MarkdownElement
import ru.skillbranch.skillarticles.data.repositories.clearContent
import ru.skillbranch.skillarticles.extensions.data.toAppSettings
import ru.skillbranch.skillarticles.extensions.data.toArticlePersonalInfo
import ru.skillbranch.skillarticles.extensions.format
import ru.skillbranch.skillarticles.extensions.indexesOf
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand
import ru.skillbranch.skillarticles.viewmodels.base.Notify
import java.util.concurrent.Executors

class ArticleViewModel(
        handle: SavedStateHandle,
        private val articleId: String
) : BaseViewModel<ArticleState>(handle, ArticleState()), IArticleViewModel {
    private val repository = ArticleRepository
    private var clearContent: String? = null
    private val listConfig by lazy {
        PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(5)
            .build()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val listData: LiveData<PagedList<CommentItemData>> =
            Transformations.switchMap(getArticleData()) {
                buildPagedList(repository.allComments(articleId, it?.commentCount ?: 0))
            }

    init {
        Log.e("ArticleViewModel", "init: ");
        //subscribe on mutable data
        subscribeOnDataSource(getArticleData()) { article, state ->
            article ?: return@subscribeOnDataSource null
            Log.e("ArticleViewModel", "author: ${article.author}");
            state.copy(
                    shareLink = article.shareLink,
                    title = article.title,
                    category = article.category,
                    categoryIcon = article.categoryIcon,
                    date = article.date.format(),
                    author = article.author
            )
        }

        subscribeOnDataSource(getArticleContent()) { content, state ->
            content ?: return@subscribeOnDataSource null
            state.copy(
                    isLoadingContent = false,
                    content = content
            )
        }

        subscribeOnDataSource(getArticlePersonalInfo()) { info, state ->
            info ?: return@subscribeOnDataSource null
            state.copy(
                    isBookmark = info.isBookmark,
                    isLike = info.isLike
            )
        }

        subscribeOnDataSource(repository.getAppSettings()) { settings, state ->
            state.copy(
                    isDarkMode = settings.isDarkMode,
                    isBigText = settings.isBigText
            )
        }

        subscribeOnDataSource(repository.isAuth()) { auth, state ->
            state.copy(isAuth = auth)
        }
    }

    //load text from network
    override fun getArticleContent(): LiveData<List<MarkdownElement>?> {
        return repository.loadArticleContent(articleId)
    }

    //load data from db
    override fun getArticleData(): LiveData<ArticleData?> {
        return repository.getArticle(articleId)
    }

    //load data from db
    override fun getArticlePersonalInfo(): LiveData<ArticlePersonalInfo?> {
        return repository.loadArticlePersonalInfo(articleId)
    }

    //app settings
    override fun handleNightMode() {
        val settings = currentState.toAppSettings()
        repository.updateSettings(settings.copy(isDarkMode = !settings.isDarkMode))
    }

    override fun handleUpText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = true))
    }

    override fun handleDownText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = false))
    }


    //personal article info
    override fun handleBookmark() {
        val info = currentState.toArticlePersonalInfo()
        repository.updateArticlePersonalInfo(info.copy(isBookmark = !info.isBookmark))

        val msg = if (currentState.isBookmark) "Add to bookmarks" else "Remove from bookmarks"
        notify(Notify.TextMessage(msg))
    }

    override fun handleLike() {
        Log.e("ArticleViewModel", "handle like: ");
        val isLiked = currentState.isLike
        val toggleLike = {
            val info = currentState.toArticlePersonalInfo()
            repository.updateArticlePersonalInfo(info.copy(isLike = !info.isLike))
        }

        toggleLike()

        val msg = if (!isLiked) Notify.TextMessage("Mark is liked")
        else {
            Notify.ActionMessage(
                    "Don`t like it anymore", //message
                    "No, still like it", //action label on snackbar
                    toggleLike // handler function , if press "No, still like it" on snackbar, then toggle again
            )
        }

        notify(msg)
    }


    //not implemented
    override fun handleShare() {
        val msg = "Share is not implemented"
        notify(Notify.ErrorMessage(msg, "OK", null))
    }


    //session state
    override fun handleToggleMenu() {
        updateState { it.copy(isShowMenu = !it.isShowMenu) }
    }

    override fun handleSearchMode(isSearch: Boolean) {
        updateState { it.copy(isSearch = isSearch, isShowMenu = false, searchPosition = 0) }
    }

    override fun handleSearch(query: String?) {
        query ?: return
        if (clearContent == null && currentState.content.isNotEmpty()) clearContent =
                currentState.content.clearContent()

        val result = clearContent
            .indexesOf(query)
            .map { it to it + query.length }
        updateState { it.copy(searchQuery = query, searchResults = result, searchPosition = 0) }
    }

    override fun handleUpResult() {
        updateState { it.copy(searchPosition = it.searchPosition.dec()) }
    }

    override fun handleDownResult() {
        updateState { it.copy(searchPosition = it.searchPosition.inc()) }
    }

    override fun handleCopyCode() {
        notify(Notify.TextMessage("Code copy to clipboard"))
    }

    override fun handleSendComment(comment: String?) {
        if (currentState.commentText == null) {
            notify(Notify.TextMessage("Comment must be not empty"))
        }
        updateState { it.copy(commentText = comment) }
        if (!currentState.isAuth) {
            navigate(NavigationCommand.StartLogin())
        } else {
            viewModelScope.launch {
                repository.sendComment(
                        articleId,
                        currentState.commentText!!,
                        currentState.answerToSlug
                )
                withContext(Dispatchers.Main) {
                    updateState {
                        it.copy(
                                answerTo = null,
                                answerToSlug = null,
                                commentText = null,
                                lastKey = null
                        )
                    }
                }
            }

        }
    }

    fun observeList(
            owner: LifecycleOwner,
            onChanged: (list: PagedList<CommentItemData>) -> Unit
    ) {
        listData.observe(owner, Observer { onChanged(it) })
    }

    fun buildPagedList(
            dataFactory: CommentsDataFactory
    ): LiveData<PagedList<CommentItemData>> {
        return LivePagedListBuilder<String, CommentItemData>(
                dataFactory,
                listConfig
        )
            .setInitialLoadKey(currentState.lastKey)
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .build()
    }

    fun handleCommentFocus(hasFocus: Boolean) {
        updateState { it.copy(showBottomBar = !hasFocus) }
    }

    fun handleClearComment() {
        updateState { it.copy(answerTo = null, answerToSlug = null, commentText = null) }
    }

    fun handleReplyTo(slug: String, name: String) {
        updateState { it.copy(answerToSlug = slug, answerTo = "Reply to $name") }
    }

    override fun saveState() {
        updateState { it.copy(lastKey = listData.value?.lastKey as? String?) }
        super.saveState()
    }

}

data class ArticleState(
        val isAuth: Boolean = false, //пользователь авторизован
        val isLoadingContent: Boolean = true, //контент загружается
        val isLoadingReviews: Boolean = true, //отзывы загружаются
        val isLike: Boolean = false, //отмечено как Like
        val isBookmark: Boolean = false, //в закладках
        val isShowMenu: Boolean = false, //отображается меню
        val isBigText: Boolean = false, //шрифт увеличен
        val isDarkMode: Boolean = false, //темный режим
        val isSearch: Boolean = false, //режим поиска
        val searchQuery: String? = null, // поисковы запрос
        val searchResults: List<Pair<Int, Int>> = emptyList(), //результаты поиска (стартовая и конечная позиции)
        val searchPosition: Int = 0, //текущая позиция найденного результата
        val shareLink: String? = null, //ссылка Share
        val title: String? = null, //заголовок статьи
        val category: String? = null, //категория
        val categoryIcon: Any? = null, //иконка категории
        val date: String? = null, //дата публикации
        val author: Any? = null, //автор статьи
        val poster: String? = null, //обложка статьи
        val content: List<MarkdownElement> = emptyList(), //контент
        val commentsCount: Int = 0,
        val answerTo: String? = null,
        val answerToSlug: String? = null,
        val showBottomBar: Boolean = true,
        val lastKey: String? = null,
        val commentText: String? = null

) : IViewModelState {
    override fun save(outState: SavedStateHandle) {
        //TODO save state
        Log.e("ArticleViewModel", "save State:lastKey $lastKey");
        outState.set("isSearch", isSearch)
        outState.set("searchQuery", searchQuery)
        outState.set("searchResults", searchResults)
        outState.set("searchPosition", searchPosition)
        outState.set("lastKey", lastKey)
        outState.set("commentText", commentText)
    }

    override fun restore(savedState: SavedStateHandle): ArticleState {
        //TODO restore state
        val value: Any? = savedState["commentText"]
        Log.e("ArticleViewModel", "restore: ${savedState.get<String>("lastKey")}");
        return copy(
                isSearch = savedState["isSearch"] ?: false,
                searchQuery = savedState["searchQuery"],
                searchResults = savedState["searchResults"] ?: emptyList(),
                searchPosition = savedState["searchPosition"] ?: 0,
                lastKey = savedState["lastKey"],
                commentText = savedState["commentText"]
        )
    }
}