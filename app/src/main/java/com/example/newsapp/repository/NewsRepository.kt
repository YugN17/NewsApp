package com.example.newsapp.repository

import com.example.newsapp.api.RetrofitInstance
import com.example.newsapp.db.ArticleDatabase
import com.example.newsapp.models.Article

class NewsRepository (
    val db:ArticleDatabase
){

    suspend fun getBreakingNews(CountryCode:String,pageNumber:Int)=RetrofitInstance.api.getBreakingNews(CountryCode,pageNumber)

    suspend fun searchNews(searchQuery:String,pageNumber: Int)=RetrofitInstance.api.searchBreakingNews(searchQuery,pageNumber)

    suspend fun insertArticle(article: Article)=db.articledao.insertArticle(article)
    suspend fun deleteArticle(article: Article)=db.articledao.deleteArticle(article)
     fun getsavednews()=db.articledao.allArticles()

}