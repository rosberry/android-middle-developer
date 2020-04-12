package ru.skillbranch.skillarticles.data.repositories

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.ArticleItemData
import ru.skillbranch.skillarticles.data.LocalDataHolder

/**
 * @author mmikhailov on 12.04.2020.
 */
object ArticlesRepository {

    fun loadArticles(): LiveData<List<ArticleItemData>?> = LocalDataHolder.findArticles()
}