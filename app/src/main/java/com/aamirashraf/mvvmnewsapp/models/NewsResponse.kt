package com.aamirashraf.mvvmnewsapp.models

import com.aamirashraf.mvvmnewsapp.models.Article

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)