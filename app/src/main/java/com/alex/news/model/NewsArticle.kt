package com.alex.news.model

data class NewsArticle (
	val source: Source,
	val author: String,
	val title: String,
	val urlToImage: String,
	val content: String
)

data class Source (
	val name: String
)
