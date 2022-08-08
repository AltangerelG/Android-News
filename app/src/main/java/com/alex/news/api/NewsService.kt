package com.alex.news.api

import com.alex.news.BuildConfig
import com.alex.news.model.NewsData
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

val apiModule = module {
    single {
        val retrofit: Retrofit = get()
        retrofit.create(NewsService::class.java)
    }
}

interface NewsService {

    @GET("everything?from=2022-07-30&sortBy=publishedAt&apiKey=" +
            BuildConfig.NEWS_API_KEY)
    suspend fun getNewsData(
        @Query("page") page: Int,
        @Query("qinTitle") title: String?
    ): NewsData
}
