package ru.skillbranch.data.remote.res


/**
 * @author neestell on 2019-12-08.
 */
data class BooksRes(
        val url: String,
        val name: String,
        val isbn: String,
        val authors: List<String>,
        val numberOfPages: Int,
        val publisher: String,
        val country: String,
        val mediaType: String,
        val released: String,
        val characters: List<String>,
        val povCharacters: List<String>
)