package com.aamirashraf.mvvmnewsapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aamirashraf.mvvmnewsapp.models.Article
import com.aamirashraf.mvvmnewsapp.models.NewsResponse
import com.aamirashraf.mvvmnewsapp.repository.NewsRepository
import com.aamirashraf.mvvmnewsapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

//@HiltViewModel

//nothings works here
//now its time to leave it
//i will back after some little hands on practice in viewmodel with dagger hilt
class NewsViewModel(
    val newsRepository:NewsRepository
):ViewModel() {
    val breakingNews:MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage=1
    var breakingNewsResponse:NewsResponse?=null
    val searchNews:MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage=1
    var searchNewsResponse:NewsResponse?=null
    init {
        getBreakingNews("in")
    }
    fun getBreakingNews(countryCode:String)=viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        val response=newsRepository.getBreakingNews("in",breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response))
    }
    fun searchNews(searchQuery:String)=viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val response=newsRepository.searchNews(searchQuery,searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
    }
    private fun handleBreakingNewsResponse(response:Response<NewsResponse>):Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let {result->
                breakingNewsPage++
                if(breakingNewsResponse==null){
                    breakingNewsResponse=result
                }else{
                    val oldArticle=breakingNewsResponse?.articles
                    val newArticle=result.articles
                    oldArticle?.addAll(newArticle)
                }
                return Resource.Success(breakingNewsResponse?:result)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response:Response<NewsResponse>):Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let {result->
                searchNewsPage++
                if(searchNewsResponse==null){
                    searchNewsResponse=result
                }else{
                    val oldArticle=searchNewsResponse?.articles
                    val newArticle=result.articles
                    oldArticle?.addAll(newArticle)
                }
                return Resource.Success(searchNewsResponse?:result)
            }
        }
        return Resource.Error(response.message())
    }
     fun savedArticle(article: Article)=viewModelScope.launch {
        newsRepository.upsert(article)
    }
    fun savedNews()=newsRepository.savedNews()
    fun deleteArticle(article: Article)=viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }
}