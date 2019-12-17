package ru.skillbranch.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import ru.skillbranch.data.remote.res.BooksRes
import ru.skillbranch.data.remote.res.CharacterRes
import ru.skillbranch.data.remote.res.HouseRes


/**
 * @author neestell on 2019-12-08.
 */
interface IceAndFireApi {

    @GET("/api/characters")
    suspend fun getCharacters(
            @Query("page") page: Int = 0,
            @Query("pageSize") pageSize: Int = 20
    ): List<CharacterRes>

    @GET("/api/books")
    suspend fun getBooks(
            @Query("pageSize") pageSize: Int = 20
    ): List<BooksRes>

    @GET("/api/houses")
    suspend fun getHouse(
            @Query("name") name: String
    ): List<HouseRes>

    @GET("/api/houses")
    suspend fun getHouses(
            @Query("page") page: Int = 1,
            @Query("pageSize") pageSize: Int = 100
    ): List<HouseRes>

    @GET
    suspend fun getCharacter(@Url url: String): CharacterRes
}