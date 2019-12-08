package ru.skillbranch.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import ru.skillbranch.data.remote.res.BooksRes
import ru.skillbranch.data.remote.res.CharacterRes
import ru.skillbranch.data.remote.res.HouseRes


/**
 * @author neestell on 2019-12-08.
 */
interface IceAndFireApi {

    @GET("/characters")
    suspend fun getCharacters(
            @Query("page") page: Int = 0,
            @Query("pageSize") pageSize: Int = 20
    ): List<CharacterRes>

    @GET("/books")
    suspend fun getBooks(
            @Query("pageSize") pageSize: Int = 20
    ): List<BooksRes>

    @GET("/houses")
    suspend fun getHouses(
    ): List<HouseRes>
}