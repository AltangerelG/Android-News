package com.alex.news.model

import android.view.View
import androidx.paging.CombinedLoadStates
import androidx.paging.PagingData
import com.alex.news.presentation.base.BaseEvent
import com.alex.news.presentation.base.BaseResult
import com.alex.news.presentation.base.BaseViewEffect
import com.alex.news.presentation.base.BaseViewState

data class ListViewState(
    val page: PagingData<NewsArticle>? = null,
    val adapterList: List<NewsArticle> = emptyList(),
    val errorMessageResource: Int? = null,
    val errorMessage: String? = null,
    val loadingStateVisibility: Int? = View.GONE,
    val errorVisibility: Int? = View.GONE
): BaseViewState

sealed class ViewEffect: BaseViewEffect {
    data class TransitionToScreen(val news: NewsArticle) : ViewEffect()
}

sealed class Event: BaseEvent {
    object SwipeToRefreshEvent: Event()
    data class LoadState(val state: CombinedLoadStates): Event()
    data class ListItemClicked(val item: NewsArticle): Event()
    // Suspended
    object ScreenLoad: Event()
}

sealed class Result: BaseResult {
    data class Error(val errorMessage: String?): Result()
    data class Content(val content: PagingData<NewsArticle>): Result()
    //data class ItemClickedResult(val item: NewsArticle, val sharedElement: View) : Result()
}