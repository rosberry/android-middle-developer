package ru.skillbranch.skillarticles.extensions.data

import ru.skillbranch.skillarticles.data.local.entities.Article
import ru.skillbranch.skillarticles.data.remote.res.ArticleDataRes
import java.util.Date

fun ArticleDataRes.toArticle(): Article = Article(
        id = id,
        title = title,
        description = description,
        author = author.toAuthor(),
        categoryId = category.id,
        poster = poster,
        date = date,
        updatedAt = Date()
)