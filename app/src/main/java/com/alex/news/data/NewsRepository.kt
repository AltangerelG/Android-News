package com.alex.news.data

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.alex.news.api.NewsService
import com.alex.news.model.NewsArticle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import org.koin.dsl.module

@ExperimentalCoroutinesApi
@FlowPreview
val repositoryModule = module {
    single { NewsRepository(get()) }
}

class NewsRepository(private val service: NewsService)  {

    companion object {
        private const val NETWORK_PAGE_SIZE = 10
    }

    fun getNews(title: String): Flow<PagingData<NewsArticle>> {
        Log.d("NewsRepository", "New page")
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { NewsPagingSource(service, title) }
        ).flow
   }
}
