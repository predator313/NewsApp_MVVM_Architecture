package com.aamirashraf.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aamirashraf.mvvmnewsapp.R
import com.aamirashraf.mvvmnewsapp.adapter.NewsAdapter
import com.aamirashraf.mvvmnewsapp.db.ArticleDatabase
import com.aamirashraf.mvvmnewsapp.repository.NewsRepository
import com.aamirashraf.mvvmnewsapp.ui.NewsActivity
import com.aamirashraf.mvvmnewsapp.ui.NewsViewModel
import com.aamirashraf.mvvmnewsapp.ui.NewsViewModelProviderFactory

class SavedNewsFragment:Fragment(R.layout.saved_news_fragment) {
    lateinit var viewModel:NewsViewModel
    lateinit var rvSavedNews:RecyclerView
    lateinit var newsAdapter: NewsAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=(activity as NewsActivity).viewModel
        val newsRepository= NewsRepository(ArticleDatabase.createDatabase(requireContext().applicationContext))
        val viewModelProviderFactory= NewsViewModelProviderFactory(newsRepository)
        viewModel= ViewModelProvider(this,viewModelProviderFactory)[NewsViewModel::class.java]
        rvSavedNews=view.findViewById(R.id.rvSavedNews)
        setupRecyclerView()

    }
    private fun setupRecyclerView(){
        newsAdapter= NewsAdapter()
        rvSavedNews.apply {
            adapter=newsAdapter
            layoutManager= LinearLayoutManager(activity)
        }

    }
}