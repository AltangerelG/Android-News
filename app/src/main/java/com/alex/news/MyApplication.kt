package com.alex.news

import android.app.Application
import com.alex.news.di.ViewModelsModule
import com.alex.news.networking.networkModule
import com.alex.news.api.apiModule
import com.alex.news.data.repositoryModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

@FlowPreview
@ExperimentalCoroutinesApi
class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(listOf(
                ViewModelsModule.modules,
                repositoryModule,
                apiModule,
                networkModule
            ))
        }
    }
}

