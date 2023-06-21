package com.aamirashraf.mvvmnewsapp.repository

import com.aamirashraf.mvvmnewsapp.api.RetrofitInstance
import com.aamirashraf.mvvmnewsapp.db.ArticleDatabase
import com.aamirashraf.mvvmnewsapp.models.Article

class NewsRepository(
    val db:ArticleDatabase
) {
    suspend fun getBreakingNews(countryCode:String,pageNumber:Int)=
        RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)
    suspend fun searchNews(searchQuery:String,pageNumber: Int)=
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)

    suspend fun upsert(article:Article)=db.getArticleDao().upsert(article)
    fun savedNews()=db.getArticleDao().getAllArticles()
    suspend fun deleteArticle(article: Article)=db.getArticleDao().delete(article)

}