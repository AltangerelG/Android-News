package com.alex.news.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.alex.news.LOG_TAG
import com.alex.news.R
import com.alex.news.databinding.FragmentDetailBinding
import com.alex.news.model.Event
import com.alex.news.model.ListViewState
import com.alex.news.viewModel.MainViewModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DetailFragment : Fragment() {

    companion object {
        private val TAG = DetailFragment::class.qualifiedName
        fun newInstance(): DetailFragment {
            return DetailFragment()
        }
    }

    private val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var binding: FragmentDetailBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
//        toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        setHasOptionsMenu(true)
        navController = Navigation.findNavController(
            requireActivity(), R.id.nav_host
        )

        requireActivity().title = "News of " + viewModel.selectedNews.value!!.source.name

        binding = FragmentDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBinding()
//        observeViewState()
        if (savedInstanceState == null) {
            lifecycleScope.launch {
                viewModel.onSuspendedEvent(Event.ScreenLoad)
            }
        }
    }

    private fun observeViewState() {
        viewModel.obtainState.observe(viewLifecycleOwner, {
            Log.d(TAG, "observeViewState obtainState result: ${it.adapterList.size}")
            render(it)
        })
    }

    private fun render(state: ListViewState) {
    }

    private fun setupBinding() {
        Log.e(LOG_TAG, viewModel.selectedNews.value.toString())
//        binding.sourceName.text = viewModel.selectedNews.value?.source?.name ?: ""
//        binding.author.text = viewModel.selectedNews.value?.author?: ""

        if(!viewModel.selectedNews.value!!.urlToImage.isNullOrEmpty()){
            Glide.with(this)
                .load(viewModel.selectedNews.value!!.urlToImage)
                .into(binding.newsImage)
        }
//        binding.descriptionText.text = viewModel.selectedNews.value?.content ?: ""
//        binding.title.text = viewModel.selectedNews.value?.title ?: ""
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            navController.navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }
}