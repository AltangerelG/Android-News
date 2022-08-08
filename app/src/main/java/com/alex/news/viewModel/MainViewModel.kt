package com.alex.news.viewModel

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.cachedIn
import com.alex.news.LOG_TAG
import com.alex.news.data.NewsRepository
import com.alex.news.model.*
import com.alex.news.presentation.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel @ExperimentalCoroutinesApi constructor(
	private val pixaBayRepository: NewsRepository
)
	: BaseViewModel<ListViewState, ViewEffect, Event, Result>(ListViewState()) {

	private var currentViewState = ListViewState()
		set(value) {
			field = value
			viewStateLD.postValue(value)
		}

	private val viewAction = MutableLiveData<ViewEffect>()

	val selectedNews = MutableLiveData<NewsArticle>()

	val obtainViewEffects: LiveData<ViewEffect> = viewAction

	val obtainState: LiveData<ListViewState> = viewState

	private fun fetchData(){
		resultToViewState(Lce.Loading())
		getNewsFlow("")
	}

	fun searchData(title: String) {
		loadJob?.cancel()
		loadJob = viewModelScope.launch(Dispatchers.IO) {
			pixaBayRepository.getNews(title)
				.cachedIn(viewModelScope)
				.onEach { ddf -> Log.d(LOG_TAG, "onScreenLoad returned FLOW: $ddf") }
				.collect { results ->
					Log.d(LOG_TAG, "collect: $results")
					resultToViewState(Lce.Content(Result.Content(results)))
				}
		}
	}

	private fun getNewsFlow(title: String) {
		Log.d(LOG_TAG, "getNewsFlow")
		loadJob?.cancel()
		loadJob = viewModelScope.launch(Dispatchers.IO) {
			pixaBayRepository.getNews(title)
				.cachedIn(viewModelScope)
				.onEach { ddf -> Log.d(LOG_TAG, "onScreenLoad returned FLOW: $ddf") }
				.collect { results ->
					Log.d(LOG_TAG, "collect: $results")
					resultToViewState(Lce.Content(Result.Content(results)))
				}
		}
	}

	override fun eventToResult(event: Event) {
		when(event) {
			is Event.ListItemClicked -> viewAction.postValue(ViewEffect.TransitionToScreen(event.item))
			is Event.LoadState -> onLoadState(event.state)
		}
	}

	private fun onLoadState(state: CombinedLoadStates) {
		// TODO: Add mapper from throwable to human readable message
		Log.d(LOG_TAG, "loading state: $state")
		when (state.source.refresh) {
			is LoadState.Error -> {
				val errorState = state.source.append as? LoadState.Error
					?: state.source.prepend as? LoadState.Error
					?: state.append as? LoadState.Error
					?: state.prepend as? LoadState.Error
				errorState?.let {
					resultToViewState(Lce.Error(Result.Error(errorMessage = errorState.error.localizedMessage)))
				}
			}
			is LoadState.Loading -> resultToViewState(Lce.Loading())
		}

	}

	override suspend fun suspendEventToResult(event: Event) {
		when(event) {
			is Event.ScreenLoad, Event.SwipeToRefreshEvent -> fetchData()
		}
	}

	override fun resultToViewState(result: Lce<Result>) {
		Log.d(LOG_TAG, "----- result $result")
		currentViewState = when (result) {
			//Loading state
			is Lce.Loading -> {
				currentViewState.copy(
					loadingStateVisibility = View.VISIBLE,
					errorVisibility = View.GONE)
			}
			//Content state
			is Lce.Content -> {
				when (result.packet) {
					is Result.Content ->
						currentViewState.copy(
							page = result.packet.content,
							loadingStateVisibility = View.GONE,
							errorVisibility = View.GONE)
					else -> currentViewState.copy()
				}
			}
			//Error state
			is Lce.Error -> {
				when (result.packet) {
					is Result.Error ->
						currentViewState.copy(
							errorVisibility = View.VISIBLE,
							errorMessage = result.packet.errorMessage,
							loadingStateVisibility = View.GONE)
					else -> currentViewState.copy()
				}
			}
		}
	}

	override fun resultToViewEffect(result: Lce<Result>) {
		TODO("Not yet implemented")
	}
}