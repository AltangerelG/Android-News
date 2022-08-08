package com.alex.news.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alex.news.LOG_TAG
import com.alex.news.R
import com.alex.news.databinding.FragmentMainBinding
import com.alex.news.model.Event
import com.alex.news.model.ListViewState
import com.alex.news.model.NewsArticle
import com.alex.news.ui.main.NewsLoadStateAdapter
import com.alex.news.ui.main.adapter.MainRecyclerViewAdapter
import com.alex.news.ui.main.callback.OnItemClickListener
import com.alex.news.ui.main.style.VerticalSpaceItemDecoration
import com.alex.news.viewModel.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@FlowPreview
@ExperimentalCoroutinesApi
class MainFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, OnItemClickListener {

	companion object {
		private val TAG = MainFragment::class.qualifiedName
		fun newInstance(): MainFragment {
			return MainFragment()
		}
	}

	private lateinit var binding: FragmentMainBinding
	private lateinit var navController: NavController
	private val adapter: MainRecyclerViewAdapter = MainRecyclerViewAdapter(this)
	// Lazy Inject ViewModel
	private val viewModel by sharedViewModel<MainViewModel>()


	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = FragmentMainBinding.inflate(inflater, container, false)
		(requireActivity() as AppCompatActivity).run {
			supportActionBar?.setDisplayHomeAsUpEnabled(false)
		}
		setHasOptionsMenu(true)
		navController = Navigation.findNavController(
			requireActivity(), R.id.nav_host
		)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setupBinding()
		observeViewState()
		if (savedInstanceState == null) {
			lifecycleScope.launch {
				viewModel.onSuspendedEvent(Event.ScreenLoad)
			}
		}
	}

	private fun setupBinding() {
		binding.swiperefresh.setOnRefreshListener(this)
		binding.list.apply {
			layoutManager = LinearLayoutManager(context)
			addItemDecoration(VerticalSpaceItemDecoration(resources.getDimensionPixelSize(com.alex.news.R.dimen.list_item_decoration)))
			initAdapter()
		}
		binding.retryButton.setOnClickListener { Log.d(LOG_TAG, "click") }

		val countrySearch = binding.newsSearch
		val searchIcon =
			countrySearch.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
		searchIcon.setColorFilter(Color.WHITE)

		val cancelIcon =
			countrySearch.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
		cancelIcon.setColorFilter(Color.WHITE)

		val textView = countrySearch.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
		textView.setTextColor(Color.WHITE)

		countrySearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
			override fun onQueryTextSubmit(query: String?): Boolean {
				return false
			}

			override fun onQueryTextChange(newText: String): Boolean {
				Log.e("searched Text: ", newText)
				viewModel.searchData(newText?: "")
				return false
			}

		})
	}

	private fun initAdapter() {
		binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
			header = NewsLoadStateAdapter { adapter.retry() },
			footer = NewsLoadStateAdapter { adapter.retry() }
		)
		adapter.addLoadStateListener {
			Log.d(LOG_TAG, "loading state: ${it.toString()}")
			viewModel.onEvent(Event.LoadState(it))
		}
		setScrollToTopWHenRefreshedFromNetwork()
	}

	private fun setScrollToTopWHenRefreshedFromNetwork() {
		// Scroll to top when the list is refreshed from network.
		lifecycleScope.launch {
			adapter.loadStateFlow
				// Only emit when REFRESH LoadState for RemoteMediator changes.
				.distinctUntilChangedBy { it.refresh }
				// Only react to cases where Remote REFRESH completes i.e., NotLoading.
				.filter { it.refresh is LoadState.NotLoading }
				.collect { binding.list.scrollToPosition(0) }
		}
	}

	private fun observeViewState() {
		viewModel.obtainState.observe(viewLifecycleOwner, {
			Log.d(TAG, "observeViewState obtainState result: ${it.adapterList.size}")
			render(it)
		})
	}

	private fun render(state: ListViewState) {
		binding.swiperefresh.isRefreshing = false
//		state.loadingStateVisibility?.let { binding.progressBar.visibility = it }
		lifecycleScope.launch {
			state.page?.let {
//				adapter = MainRecyclerAdapter(requireContext(), it, this)
				adapter.submitData(it)
			}
		}
		state.errorVisibility?.let {
			binding.mainListErrorMsg.visibility = it
			binding.retryButton.visibility = it
			state.errorMessage?.let { binding.mainListErrorMsg.text = state.errorMessage }
			state.errorMessageResource?.let { binding.mainListErrorMsg.text = getString(state.errorMessageResource) }
		}
	}

	override fun onRefresh() {
		lifecycleScope.launch {
			viewModel.onSuspendedEvent(Event.ScreenLoad)
		}
	}

	override fun onItemClick(item: NewsArticle?) {
		if (item != null) {
			Log.e("LOG", "Selected news: ${item.source.name}")
		}
		viewModel.selectedNews.value = item
		navController.navigate(R.id.action_nav_detail)
	}
}