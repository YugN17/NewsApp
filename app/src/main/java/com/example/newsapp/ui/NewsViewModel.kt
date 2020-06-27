package com.example.newsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.NewsApplication
import com.example.newsapp.models.Article
import com.example.newsapp.models.NewsResponse
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel
    (application: Application,
    val newsrepo: NewsRepository)
    :AndroidViewModel(application) {

    val brerakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breaking_page = 1
    var breakingNewsResponse:NewsResponse?=null

    val searchingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searching_page = 1
    var searchingNewsResponse:NewsResponse?=null

    init {
        getBreaking("us")
    }

    fun getBreaking(countrycode: String) = viewModelScope.launch {
        safeBrakingNewsCall(countrycode)

    }

    fun getSearching(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }


    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {

            response.body()?.let { result ->
                breaking_page++
                if(breakingNewsResponse==null){

                    breakingNewsResponse=result
                }else{
                    val oldArticle= breakingNewsResponse!!.articles
                    val newArticle=result.articles
                    oldArticle.addAll(newArticle)
                }

                return Resource.Success(breakingNewsResponse?: result)
            }

        }
        return Resource.Error(response.message())

    }

    private fun SearchingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {

            response.body()?.let { result ->
                searching_page++
                if(searchingNewsResponse==null){

                    searchingNewsResponse=result
                }else{
                    val oldArticle= searchingNewsResponse!!.articles
                    val newArticle=result.articles
                    oldArticle.addAll(newArticle)
                }

                return Resource.Success(searchingNewsResponse?: result)
            }

        }
        return Resource.Error(response.message())

    }
    fun saveArticle(article: Article)=viewModelScope.launch {
        newsrepo.insertArticle(article)
    }
    fun deleteArticle(article: Article)=viewModelScope.launch {
        newsrepo.deleteArticle(article)
    }
    fun getAllArticles()=newsrepo.getsavednews()

    private suspend fun safeBrakingNewsCall(countrycode: String){
brerakingNews.postValue(Resource.Loading())
        try {
if(hasInternetConnection()) {
    val response = newsrepo.getBreakingNews(countrycode, breaking_page)
    brerakingNews.postValue(handleBreakingNewsResponse(response))
}else{
    brerakingNews.postValue(Resource.Error("No Internet Connection"))
}
        }catch (t:Throwable){

            when(t){
                is IOException->  brerakingNews.postValue(Resource.Error("Network Failure"))
                else->  brerakingNews.postValue(Resource.Error("Conversion Error"))
            }

        }

    }
    private suspend fun safeSearchNewsCall(query: String){
        searchingNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()) {
                val response = newsrepo.searchNews(query, searching_page)
                searchingNews.postValue(SearchingNewsResponse(response))
            }else{
                searchingNews.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t:Throwable){

            when(t){
                is IOException->  searchingNews.postValue(Resource.Error("Network Failure"))
                else->  searchingNews.postValue(Resource.Error("Conversion Error"))
            }

        }

    }

    private fun hasInternetConnection():Boolean{
val connectivityManager=getApplication<NewsApplication>().getSystemService(
Context.CONNECTIVITY_SERVICE
) as ConnectivityManager
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            val activeNetwork=connectivityManager.activeNetwork?:return false
            val capabilities=connectivityManager.getNetworkCapabilities(activeNetwork)?:return false

            return when{
                capabilities.hasTransport(TRANSPORT_WIFI)->true
                capabilities.hasTransport(TRANSPORT_CELLULAR)->true
                capabilities.hasTransport(TRANSPORT_ETHERNET)->true
                else->false

            }
        }else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI->true
                    TYPE_MOBILE->true
                    TYPE_ETHERNET->true
                    else->false

                }
            }
        }
return false
    }
}