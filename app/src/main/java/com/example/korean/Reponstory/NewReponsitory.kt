package com.example.korean.Reponstory

import com.example.korean.api.RetronInStance
import com.example.korean.db.ArticleDatabase
import com.example.korean.model.Article

class NewReponsitory(val db: ArticleDatabase) {
    suspend fun getHeadlines(contryCode: String, pageNumber: Int) =
        RetronInStance.api.getHeadlines(contryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetronInStance.api.searchEverything(searchQuery, pageNumber)

    suspend fun upset(article: Article) = db.getArticleDao().upsert(article)

    fun getFavouriteNews() = db.getArticleDao().getAllArticle()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

}