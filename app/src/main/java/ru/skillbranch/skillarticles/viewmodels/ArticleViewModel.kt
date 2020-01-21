package ru.skillbranch.skillarticles.viewmodels

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.extensions.data.toAppSettings
import ru.skillbranch.skillarticles.extensions.data.toArticlePersonalInfo
import ru.skillbranch.skillarticles.extensions.format

class ArticleViewModel(private val articleId: String) : BaseViewModel<ArticleState>(ArticleState()) {

    private val repository = ArticleRepository

    init {
        subscribeOnDataSource(getArticleData()) { data, state ->
            data ?: return@subscribeOnDataSource null
            state.copy(
                    shareLink = data.shareLink,
                    title = data.title,
                    category = data.category,
                    categoryIcon = data.categoryIcon,
                    date = data.date.format(),
                    author = data.author
            )
        }

        subscribeOnDataSource(getArticleContent()) { data, state ->
            data ?: return@subscribeOnDataSource null
            state.copy(
                    isLoadingContent = false,
                    content = data
            )
        }

        subscribeOnDataSource(getArticlePersonalInfo()) { data, state ->
            data ?: return@subscribeOnDataSource null
            state.copy(
                    isLike = data.isLike,
                    isBookmark = data.isBookmark
            )
        }

        subscribeOnDataSource(repository.getAppSettings()) { data, state ->
            state.copy(
                    isDarkMode = data.isDarkMode,
                    isBigText = data.isBigText
            )
        }
    }

    fun getArticleData(): LiveData<ArticleData?> {
        return repository.getArticle(articleId)
    }

    fun getArticleContent(): LiveData<List<Any>?> {
        return repository.loadArticleContent(articleId)
    }

    fun getArticlePersonalInfo(): LiveData<ArticlePersonalInfo?> {
        return repository.loadArticlePersonalInfo(articleId)
    }

    fun handleNightMode() {
        val settings = currentState.toAppSettings()
        repository.updateSettings(settings.copy(isDarkMode = !settings.isDarkMode))
    }

    fun handleUpText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = true))
    }

    fun handleDownText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = false))
    }

    fun handleLike() {
        val toggleLike = {
            val info = currentState.toArticlePersonalInfo()
            repository.updateArticlePersonalInfo(info.copy(isLike = !info.isLike))
        }

        toggleLike()

        val msg = if (currentState.isLike) Notify.TextMessage("Mark is liked") else {
            Notify.ActionMessage(
                    "Don`t like it anymore",
                    "No, still like it",
                    toggleLike
            )
        }

        notify(msg)
    }

    fun handleBookmark() {
        val info = currentState.toArticlePersonalInfo()
        repository.updateArticlePersonalInfo(info.copy(isBookmark = !info.isBookmark))
        val msg = if (currentState.isBookmark) "Add to bookmarks" else "Remove from bookmarks"

        notify(Notify.TextMessage(msg))
    }

    fun handleShare() {
        val msg = "Share is not implemented"
        notify(Notify.ErrorMessage(msg, "OK", null))
    }

    fun handleToggleMenu() {
        updateState { it.copy(isShowMenu = !it.isShowMenu) }
    }
}

data class ArticleState(
        val isAuth: Boolean = false,
        val isLoadingContent: Boolean = true,
        val isLoadingReviews: Boolean = true,
        val isLike: Boolean = false,
        val isBookmark: Boolean = false,
        val isShowMenu: Boolean = false,
        val isBigText: Boolean = false,
        val isDarkMode: Boolean = false,
        val isSearch: Boolean = false,
        val searchQuery: String? = null,
        val searchResults: List<Pair<Int, Int>> = emptyList(),
        val searchPosition: Int = 0,
        val shareLink: String? = null,
        val title: String? = null,
        val category: String? = null,
        val categoryIcon: Any? = null,
        val date: String? = null,
        val author: Any? = null,
        val poster: String? = null,
        val content: List<Any> = emptyList(),
        val reviews: List<Any> = emptyList()
)
