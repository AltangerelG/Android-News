package com.alex.news.ui.main.callback

import com.alex.news.model.NewsArticle

interface OnItemClickListener {
    fun onItemClick(item: NewsArticle?)
}