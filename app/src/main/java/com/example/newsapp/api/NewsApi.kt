package com.example.newsapp.api

import com.example.newsapp.models.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode:String="us",
        @Query("page")
        pageNum:Int=1,
        @Query("apiKey")
        apiKey:String="c872d7b17ef74df0915610f6a0e91b9f"):Response<NewsResponse>


    @GET("v2/everything")
    suspend fun searchBreakingNews(
        @Query("q")
        query:String,
        @Query("page")
        pageNum:Int=1,
        @Query("apiKey")
        apiKey:String="c872d7b17ef74df0915610f6a0e91b9f"):Response<NewsResponse>

}