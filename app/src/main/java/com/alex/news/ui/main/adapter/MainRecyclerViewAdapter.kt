package com.alex.news.ui.main.adapter

import android.util.Log
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.alex.news.model.NewsArticle
import com.alex.news.ui.main.callback.OnItemClickListener
import com.alex.news.ui.main.types.NewsViewHolder
import java.util.*

class MainRecyclerViewAdapter(
    private val listener: OnItemClickListener
) : PagingDataAdapter<NewsArticle, NewsViewHolder>(NEWS_COMPARATOR), Filterable {

    var newsFilterList: List<NewsArticle>

    init {
        Log.e("data", snapshot().items.toString())
        newsFilterList = snapshot().items
    }

    companion object {
        private val NEWS_COMPARATOR = object : DiffUtil.ItemCallback<NewsArticle>() {
            override fun areItemsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder.create(parent, listener)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        getItem(position)?.let { news ->
            holder.bind(news)
        }
    }

    override fun getItemCount() = snapshot().items.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    newsFilterList = snapshot().items
                } else {
                    val resultList = ArrayList<NewsArticle>()
//                    viewModel.searchData(charSearch)

                    for (row in snapshot().items) {
                        if (row.title.lowercase(Locale.ROOT)
                                .contains(charSearch.lowercase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    Log.e("searchedList:", resultList.toString())
                    newsFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = newsFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                newsFilterList = results?.values as ArrayList<NewsArticle>
                notifyDataSetChanged()
            }

        }
    }
}
