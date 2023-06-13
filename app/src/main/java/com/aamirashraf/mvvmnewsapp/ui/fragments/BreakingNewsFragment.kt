package com.aamirashraf.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.os.RecoverySystem
import android.util.Log
import android.view.View
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
        val newsRepository= NewsRepository(ArticleDatabase.createDatabase(requireContext().applicationContext))
        val viewModelProviderFactory=NewsViewModelProviderFactory(newsRepository)
        viewModel=ViewModelProvider(this,viewModelProviderFactory)[NewsViewModel::class.java]

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
                        newsAdapter.differ.submitList(newsResponse.articles)
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
    }
    private fun showProgressBar(){
        paginationProgressBar.visibility=View.VISIBLE
    }

    private fun setupRecyclerView(){
       newsAdapter= NewsAdapter()
       rvBreakingNews.apply {
           adapter=newsAdapter
           layoutManager=LinearLayoutManager(activity)
       }

    }
}