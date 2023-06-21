package com.aamirashraf.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.aamirashraf.mvvmnewsapp.R
import com.aamirashraf.mvvmnewsapp.db.ArticleDatabase
import com.aamirashraf.mvvmnewsapp.repository.NewsRepository
import com.aamirashraf.mvvmnewsapp.ui.NewsActivity
import com.aamirashraf.mvvmnewsapp.ui.NewsViewModel
import com.aamirashraf.mvvmnewsapp.ui.NewsViewModelProviderFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class ArticleFragment:Fragment(R.layout.article_fragment) {
    lateinit var viewModel:NewsViewModel
    lateinit var fab: FloatingActionButton
    val args:ArticleFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel=(activity as NewsActivity).viewModel
        val newsRepository= NewsRepository(ArticleDatabase.createDatabase(requireContext()))
        val viewModelProviderFactory= NewsViewModelProviderFactory(newsRepository)
        viewModel= ViewModelProvider(requireActivity(),viewModelProviderFactory)[NewsViewModel::class.java]
//        viewModel=(activity as NewsActivity).viewModel

        val article=args.article
        val webView=view.findViewById<WebView>(R.id.webView)
        webView.apply {
            webViewClient= WebViewClient()//meas inside this web view not browser webview
            loadUrl(article.url!!)
        }
        fab=view.findViewById(R.id.fab)
        fab.setOnClickListener {
//            viewModel.savedNews()
            viewModel.savedArticle(article)
            Snackbar.make(view,"Article saved successfully",Snackbar.LENGTH_SHORT).show()
        }
    }
}