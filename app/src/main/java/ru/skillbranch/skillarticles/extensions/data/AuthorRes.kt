package ru.skillbranch.skillarticles.extensions.data

import ru.skillbranch.skillarticles.data.local.entities.Author
import ru.skillbranch.skillarticles.data.remote.res.AuthorRes

/**
 * @author mmikhailov on 17.08.2020.
 */
fun AuthorRes.toAuthor(): Author = Author(
        userId = id,
        avatar = avatar,
        name = name
)