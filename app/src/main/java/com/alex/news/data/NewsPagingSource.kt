package com.alex.news.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.alex.news.LOG_TAG
import com.alex.news.api.NewsService
import com.alex.news.model.NewsArticle
import retrofit2.HttpException
import java.io.IOException

private const val NEWS_STARTING_PAGE_INDEX = 1

class NewsPagingSource(
    private val service: NewsService,
    private val title: String
) : PagingSource<Int, NewsArticle>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsArticle> {
        val position = params.key ?: NEWS_STARTING_PAGE_INDEX
        return try {
            var text = ""
            if (title.isEmpty()) {
                // since i should pull android related articles, by default I'm fetching
                // android related articles if searchView is empty
                text = "android"
            } else text = title
            val response = service.getNewsData(position, text)
            val news = response.articles
            Log.d(LOG_TAG, "Service -> getNews: ${news.size}")
            LoadResult.Page(
                data = news,
                prevKey = if (position == NEWS_STARTING_PAGE_INDEX) null else position,
                nextKey = if (news.isEmpty()) null else position + 1

            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, NewsArticle>): Int? {
        TODO("Not yet implemented")
    }
}