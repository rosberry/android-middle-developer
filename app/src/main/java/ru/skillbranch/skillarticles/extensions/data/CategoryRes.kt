package ru.skillbranch.skillarticles.extensions.data

import ru.skillbranch.skillarticles.data.local.entities.Category
import ru.skillbranch.skillarticles.data.remote.res.CategoryRes

/**
 * @author mmikhailov on 17.08.2020.
 */
fun CategoryRes.toCategory(): Category = Category(
        categoryId = id,
        icon = icon,
        title = title
)