package com.aamirashraf.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aamirashraf.mvvmnewsapp.R
import com.aamirashraf.mvvmnewsapp.adapter.NewsAdapter
import com.aamirashraf.mvvmnewsapp.db.ArticleDatabase
import com.aamirashraf.mvvmnewsapp.repository.NewsRepository
import com.aamirashraf.mvvmnewsapp.ui.NewsActivity
import com.aamirashraf.mvvmnewsapp.ui.NewsViewModel
import com.aamirashraf.mvvmnewsapp.ui.NewsViewModelProviderFactory
import com.aamirashraf.mvvmnewsapp.utils.Constants
import com.aamirashraf.mvvmnewsapp.utils.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.aamirashraf.mvvmnewsapp.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment:Fragment(R.layout.search_news_fragment) {
    lateinit var viewModel: NewsViewModel
    lateinit var rvSearchNews:RecyclerView
    lateinit var newsAdapter: NewsAdapter
    lateinit var paginationProgressBar:ProgressBar
    lateinit var etSearchNews:EditText
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel=(activity as NewsActivity).viewModel
        val newsRepository= NewsRepository(ArticleDatabase.createDatabase(requireContext()))
        val viewModelProviderFactory= NewsViewModelProviderFactory(newsRepository)
        viewModel= ViewModelProvider(requireActivity(),viewModelProviderFactory)[NewsViewModel::class.java]
//        viewModel=(activity as NewsActivity).viewModel
        etSearchNews=view.findViewById(R.id.etSearch)

        rvSearchNews=view.findViewById(R.id.rvSearchNews)
        paginationProgressBar=view.findViewById(R.id.paginationProgressBarsearch)
        setupRecyclerView()
        newsAdapter.setOnItemClickListener {
            val bundle=Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }
        var job:Job?=null
        etSearchNews.addTextChangedListener {editable->
            job?.cancel()
            job= MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if(editable.toString().isNotEmpty()){
                        viewModel.searchNews(editable.toString())
                    }
                }

            }

        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer{ response->
            when(response){
                is Resource.Success->{
                    hideProgressBar()
                    response.data?.let {newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())//tolist is imporant
                        //otherwise it cant be work with the mutable live data
                        //this is important to handle last page
                        val totalPages=newsResponse.totalResults/ Constants.QUERY_PAGE_SIZE +2
                        isLastPage= viewModel.searchNewsPage == totalPages
                        if(isLastPage){
                            //this provide 50dp of empty space at the bottom of the recycler view
                            rvSearchNews.setPadding(0,0,0,0)
                        }
                    }
                }

                is Resource.Error->{
                    hideProgressBar()
                    response.message?.let {message->
                        Log.e("SearchNewsFragment","an error occur${message}")
                    }
                }

                is Resource.Loading->{
                    showProgressBar()
                }
            }

        })
    }
    private fun hideProgressBar() {
        paginationProgressBar.visibility=View.INVISIBLE
        isLoading=false
    }
    private fun showProgressBar(){
        paginationProgressBar.visibility=View.VISIBLE
        isLoading=true
    }
    var isLoading=false
    var isLastPage=false
    var isScrolling=false
    var scrollListener=object: RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val isNotLoadingAndNotInLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBegining = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotInLastPage && isAtLastItem &&
                    isNotAtBegining && isTotalMoreThanVisible
                    && isScrolling
            if (shouldPaginate) {
                viewModel.searchNews(etSearchNews.text.toString())
                isScrolling = false
            }

        }
    }


    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }

    }
}
