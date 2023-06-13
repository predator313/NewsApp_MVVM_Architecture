package com.aamirashraf.mvvmnewsapp.models

import com.aamirashraf.mvvmnewsapp.models.Article

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)