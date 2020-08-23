package ru.skillbranch.skillarticles.data.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.skillbranch.skillarticles.data.remote.req.LoginReq
import ru.skillbranch.skillarticles.data.remote.req.MessageReq
import ru.skillbranch.skillarticles.data.remote.req.RefreshReq
import ru.skillbranch.skillarticles.data.remote.res.ArticleContentRes
import ru.skillbranch.skillarticles.data.remote.res.ArticleCountsRes
import ru.skillbranch.skillarticles.data.remote.res.ArticleRes
import ru.skillbranch.skillarticles.data.remote.res.AuthRes
import ru.skillbranch.skillarticles.data.remote.res.BookmarkRes
import ru.skillbranch.skillarticles.data.remote.res.CommentRes
import ru.skillbranch.skillarticles.data.remote.res.LikeRes
import ru.skillbranch.skillarticles.data.remote.res.MessageRes
import ru.skillbranch.skillarticles.data.remote.res.RefreshRes

/**
 * @author mmikhailov on 17.08.2020.
 */
interface RestService {

    // https://skill-articles.skill-branch.ru/api/v1/articles?last=articleId&limit=10
    @GET("articles")
    suspend fun articles(
            @Query("last") last: String? = null,
            @Query("limit") limit: Int = 10
    ): List<ArticleRes>

    // https://skill-articles.skill-branch.ru/api/v1/articles/{articleId}/content
    @GET("articles/{article}/content")
    suspend fun loadArticleContent(@Path("article") articleId: String): ArticleContentRes

    // https://skill-articles.skill-branch.ru/api/v1/articles/{articleId}/messages
    @GET("articles/{article}/messages")
    fun loadComments(
            @Path("article") articleId: String,
            @Query("last") last: String? = null,
            @Query("limit") limit: Int = 5
    ): Call<List<CommentRes>>

    // https://skill-articles.skill-branch.ru/api/v1/articles/{articleId}/messages
    @POST("articles/{article}/messages")
    suspend fun sendMessage(
            @Path("article") articleId: String,
            @Body message: MessageReq,
            @Header("Authorization") token: String
    ): MessageRes

    // https://skill-articles.skill-branch.ru/api/v1/articles/{articleId}/counts
    @GET("articles/{article}/counts")
    suspend fun loadArticleCounts(@Path("article") articleId: String): ArticleCountsRes

    // https://skill-articles.skill-branch.ru/api/v1/auth/login
    @POST("auth/login")
    suspend fun login(@Body loginReq: LoginReq): AuthRes

    @POST("auth/login")
    fun loginCall(@Body loginReq: LoginReq): Call<AuthRes>

    // https://skill-articles.skill-branch.ru/api/v1/articles/{articleId}/decrementLikes
    @POST("articles/{article}/decrementLikes")
    suspend fun decrementLike(
            @Path("article") articleId: String,
            @Header("Authorization") token: String
    ): LikeRes

    // https://skill-articles.skill-branch.ru/api/v1/articles/{articleId}/incrementLikes
    @POST("articles/{article}/incrementLikes")
    suspend fun incrementLike(
            @Path("article") articleId: String,
            @Header("Authorization") token: String
    ): LikeRes

    // https://skill-articles.skill-branch.ru/api/v1/articles/{articleId}/addBookmark
    @POST("articles/{article}/addBookmark")
    suspend fun addBookmark(
            @Path("article") articleId: String,
            @Header("Authorization") token: String
    ): BookmarkRes

    // https://skill-articles.skill-branch.ru/api/v1/articles/{articleId}/removeBookmark
    @POST("articles/{article}/removeBookmark")
    suspend fun removeBookmark(
            @Path("article") articleId: String,
            @Header("Authorization") token: String
    ): BookmarkRes

    @POST("auth/refresh")
    fun refreshAccessToken(
            @Body refresh: RefreshReq
    ): Call<RefreshRes>
}