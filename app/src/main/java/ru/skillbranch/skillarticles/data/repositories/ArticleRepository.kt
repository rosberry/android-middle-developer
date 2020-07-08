package ru.skillbranch.skillarticles.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import ru.skillbranch.skillarticles.data.LocalDataHolder
import ru.skillbranch.skillarticles.data.NetworkDataHolder
import ru.skillbranch.skillarticles.data.models.AppSettings
import ru.skillbranch.skillarticles.data.models.ArticleData
import ru.skillbranch.skillarticles.data.models.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.models.CommentItemData
import ru.skillbranch.skillarticles.data.models.User
import java.lang.Thread.sleep
import kotlin.math.abs

object ArticleRepository {
    private val local = LocalDataHolder
    private val network = NetworkDataHolder

    fun loadArticleContent(articleId: String): LiveData<List<MarkdownElement>?> {
        return Transformations.map(network.loadArticleContent(articleId)) {
            return@map if (it == null) null
            else MarkdownParser.parse(it)
        }
    }

    fun getArticle(articleId: String): LiveData<ArticleData?> {
        return local.findArticle(articleId) //2s delay from db
    }

    fun loadArticlePersonalInfo(articleId: String): LiveData<ArticlePersonalInfo?> {
        return local.findArticlePersonalInfo(articleId) //1s delay from db
    }

    fun getAppSettings(): LiveData<AppSettings> = local.getAppSettings() //from preferences
    fun updateSettings(appSettings: AppSettings) {
        local.updateAppSettings(appSettings)
    }

    fun updateArticlePersonalInfo(info: ArticlePersonalInfo) {
        local.updateArticlePersonalInfo(info)
    }

    fun isAuth(): MutableLiveData<Boolean> = local.isAuth()

    fun allComments(articleId: String, totalCount: Int) =
            CommentsDataFactory(
                    itemProvider = ::loadCommentsByRange,
                    articleId = articleId,
                    totalCount = totalCount
            )

    fun loadCommentsByRange(
            slug: String?,
            size: Int,
            articleId: String
    ): Pair<List<CommentItemData>, Int> {
        val data = network.commentsData.getOrElse(articleId) { mutableListOf() }
        return when {
            slug == null -> data.take(size) to 0

            size > 0 -> {
                val res = data.dropWhile { it.slug != slug }
                    .drop(1)
                    .take(size)

                res to data.indexOfFirst { it.slug == slug }
                    .inc()
            }

            size < 0 -> {
                val res = data
                    .dropLastWhile { it.slug != slug }
                    .dropLast(1)
                    .takeLast(abs(size))

                Log.e(
                        "ArticleRepository",
                        "pos ${data.indexOfFirst { it.slug == slug }
                            .minus(abs(size).inc())}: ${data.map { it.slug }} "
                );
                val pos = data.indexOfFirst { it.slug == slug }
                    .minus(abs(size))
                res to if (pos < 0) 0 else pos
            }

            else -> emptyList<CommentItemData>() to 0
        }.apply { sleep(500) }
    }

    fun sendComment(articleId: String, comment: String, answerToSlug: String?) {
        network.sendMessage(
                articleId, comment, answerToSlug,
                User("777", "John Doe", "https://skill-branch.ru/img/mail/bot/android-category.png")
        )
        local.incrementCommentsCount(articleId)
    }
}

class CommentsDataFactory(
        private val itemProvider: (String?, Int, String) -> Pair<List<CommentItemData>, Int>,
        private val articleId: String,
        private val totalCount: Int
) : DataSource.Factory<String?, CommentItemData>() {
    override fun create(): DataSource<String?, CommentItemData> =
            CommentsDataSource(itemProvider, articleId, totalCount)

}

class CommentsDataSource(
        private val itemProvider: (String?, Int, String) -> Pair<List<CommentItemData>, Int>,
        private val articleId: String,
        private val totalCount: Int
) : ItemKeyedDataSource<String, CommentItemData>() {

    override fun loadInitial(
            params: LoadInitialParams<String>,
            callback: LoadInitialCallback<CommentItemData>
    ) {
        var result = itemProvider(
                params.requestedInitialKey,
                params.requestedLoadSize,
                articleId
        )
        if (result.first.isEmpty()) {
            result = itemProvider(
                    params.requestedInitialKey,
                    -params.requestedLoadSize,
                    articleId
            )
        }
        Log.e(
                "ArticleRepository",
                "loadInitial: key > ${params.requestedInitialKey} size > ${result.first.size} totalCount > $totalCount pos > ${result.second}"
        );
        callback.onResult(
                if (totalCount > 0) result.first else emptyList(),
                result.second,
                totalCount
        )
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<CommentItemData>) {
        val result = itemProvider(params.key, params.requestedLoadSize, articleId)
        Log.e("ArticleRepository", "loadAfter: key > ${params.key} size > ${result.first.size} ");
        callback.onResult(result.first)
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<CommentItemData>) {
        val result = itemProvider(params.key, -params.requestedLoadSize, articleId)
        Log.e("ArticleRepository", "loadBefore: key > ${params.key} size > ${result.first.size} ");
        callback.onResult(result.first)
    }

    override fun getKey(item: CommentItemData): String = item.slug

}
