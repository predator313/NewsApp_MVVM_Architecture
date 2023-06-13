package com.aamirashraf.mvvmnewsapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.aamirashraf.mvvmnewsapp.R
import com.aamirashraf.mvvmnewsapp.databinding.ActivityNewsBinding
import com.aamirashraf.mvvmnewsapp.db.ArticleDatabase
import com.aamirashraf.mvvmnewsapp.db.ArticleDatabase.Companion.createDatabase
import com.aamirashraf.mvvmnewsapp.repository.NewsRepository



class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    lateinit var viewModel: NewsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityNewsBinding.inflate(layoutInflater)
//        val view=binding.root
        setContentView(binding.root)
        val newsRepository=NewsRepository(createDatabase(this))
        val viewModelProviderFactory=NewsViewModelProviderFactory(newsRepository)
        viewModel=ViewModelProvider(this,viewModelProviderFactory)[NewsViewModel::class.java]

//         val navHostFragment=supportFragmentManager.findFragmentById(R.id.news_nav_host_fragment)
//            binding.bottomNavigationView.setupWithNavController(navHostFragment!!.findNavController())
        val navHostFragment=supportFragmentManager.findFragmentById(R.id.news_nav_host_fragment)
        binding.bottomNavigationView.setupWithNavController(navHostFragment!!.findNavController())
    }
}
