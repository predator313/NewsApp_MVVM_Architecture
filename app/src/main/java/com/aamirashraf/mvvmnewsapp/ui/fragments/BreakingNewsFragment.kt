package com.aamirashraf.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.os.RecoverySystem
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.ProgressBar
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aamirashraf.mvvmnewsapp.R
import com.aamirashraf.mvvmnewsapp.adapter.NewsAdapter
import com.aamirashraf.mvvmnewsapp.api.RetrofitInstance
import com.aamirashraf.mvvmnewsapp.db.ArticleDatabase
import com.aamirashraf.mvvmnewsapp.repository.NewsRepository
import com.aamirashraf.mvvmnewsapp.ui.NewsActivity
import com.aamirashraf.mvvmnewsapp.ui.NewsViewModel
import com.aamirashraf.mvvmnewsapp.ui.NewsViewModelProviderFactory
import com.aamirashraf.mvvmnewsapp.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.aamirashraf.mvvmnewsapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

//@AndroidEntryPoint
class BreakingNewsFragment:Fragment(R.layout.breaking_news_fragment) {
    lateinit var viewModel:NewsViewModel

    lateinit var newsAdapter: NewsAdapter
    lateinit var rvBreakingNews:RecyclerView
    lateinit var paginationProgressBar:ProgressBar
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel=(activity as NewsActivity).viewModel
        val newsRepository= NewsRepository(ArticleDatabase.createDatabase(requireContext().applicationContext))
        val viewModelProviderFactory=NewsViewModelProviderFactory(newsRepository)
        viewModel=ViewModelProvider(requireActivity(),viewModelProviderFactory)[NewsViewModel::class.java]

        rvBreakingNews=view.findViewById(R.id.rvBreakingNews)
        paginationProgressBar=view.findViewById(R.id.paginationProgressBar)
        setupRecyclerView()
        newsAdapter.setOnItemClickListener {
            val bundle=Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer{ response->
            when(response){
                is Resource.Success->{
                    hideProgressBar()
                    response.data?.let {newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())//tolist is imporant
                        //otherwise it cant be work with the mutable live data
                        //this is important to handle last page
                        val totalPages=newsResponse.totalResults/ QUERY_PAGE_SIZE +2
                        isLastPage= viewModel.breakingNewsPage == totalPages
                        if(isLastPage){
                            rvBreakingNews.setPadding(0,0,0,0)
                        }

                    }
                }

                is Resource.Error->{
                    hideProgressBar()
                    response.message?.let {message->
                        Log.e("BreakingNewsFragment","an error occur${message}")
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
    var scrollListener=object: RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling=true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager=recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition=layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount=layoutManager.childCount
            val totalItemCount=layoutManager.itemCount
            val isNotLoadingAndNotInLastPage=!isLoading && !isLastPage
            val isAtLastItem=firstVisibleItemPosition+visibleItemCount>=totalItemCount
            val isNotAtBegining=firstVisibleItemPosition>=0
            val isTotalMoreThanVisible=totalItemCount>=QUERY_PAGE_SIZE
            val shouldPaginate=isNotLoadingAndNotInLastPage && isAtLastItem &&
                    isNotAtBegining && isTotalMoreThanVisible
                    && isScrolling
            if (shouldPaginate){
                viewModel.getBreakingNews("in")
                isScrolling=false
            }

        }


    }




    private fun setupRecyclerView(){
        newsAdapter= NewsAdapter()
        rvBreakingNews.apply {
            adapter=newsAdapter
            //activity and requiredActivity() is same as both reference the host activity of this fragment
            layoutManager=LinearLayoutManager(activity)
            //we use this@breakingNewsFragment.scrollListner

            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }

    }
}