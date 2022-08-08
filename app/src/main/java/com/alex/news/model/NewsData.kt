package com.alex.news.model

data class NewsData (
    val status: String,
    val totalResults: Int,
    val articles: List<NewsArticle>
)