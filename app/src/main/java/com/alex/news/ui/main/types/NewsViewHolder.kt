/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alex.news.ui.main.types

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alex.news.R
import com.alex.news.model.NewsArticle
import com.alex.news.ui.main.callback.OnItemClickListener
import com.bumptech.glide.Glide


/**
 * View Holder for a [NewsArticle] RecyclerView list item.
 */
class NewsViewHolder(view: View, private val listener: OnItemClickListener) : RecyclerView.ViewHolder(view) {
    private val sourceName: TextView = view.findViewById(R.id.sourceName)
    private val author: TextView = view.findViewById(R.id.author)
    private val title: TextView = view.findViewById(R.id.title)
    private val newsImage: ImageView = view.findViewById(R.id.newsImage)
    private var news: NewsArticle? = null
    private var view: View = view

    init {
        view.setOnClickListener {
            listener.onItemClick(news)
        }
    }

    fun bind(news: NewsArticle?) {
        if (news == null) {
            val resources = itemView.resources
            sourceName.text = resources.getString(R.string.loading)
            author.text = resources.getString(R.string.loading)
            title.text = resources.getString(R.string.loading)
        } else {
            showData(news)
        }
    }

    private fun showData(news: NewsArticle) {
        this.news= news
        sourceName.text = news.source.name
        author.text = news.author
        title.text = news.title

        if(!news.urlToImage.isNullOrEmpty()){
            Glide.with(view.context)
                .load(news.urlToImage)
                .into(newsImage)
        }
    }

    companion object {
        fun create(parent: ViewGroup, listener: OnItemClickListener): NewsViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.news_list_item, parent, false)
            return NewsViewHolder(view, listener)
        }
    }
}
