package com.aamirashraf.mvvmnewsapp.adapter

import android.net.wifi.p2p.WifiP2pManager.NetworkInfoListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aamirashraf.mvvmnewsapp.R
import com.aamirashraf.mvvmnewsapp.databinding.ItemArticlePreviewBinding
import com.aamirashraf.mvvmnewsapp.models.Article
import com.bumptech.glide.Glide

class NewsAdapter:RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {
//    inner class ArticleViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
//
//    }
    inner class ArticleViewHolder(val binding: ItemArticlePreviewBinding):RecyclerView.ViewHolder(binding.root)

    //good practice is always use diff utils
    //diff utils execute in background
    //using the call back
    private var differCallback=object :DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {

          return  oldItem.url==newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
           return oldItem==newItem
        }

    }
    val differ=AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
//        return ArticleViewHolder(
//            LayoutInflater.from(parent.context)
//                .inflate(
//                    R.layout.item_article_preview,
//                    parent,
//                    false
//                )
//        )
        val binding=ItemArticlePreviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticleViewHolder(binding)
    }

    override fun getItemCount(): Int {
       //since we don't have list so we return the current list size from the list differ
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article=differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(holder.binding.ivArticleImage)
            holder.binding.tvSource.text=article.source?.name
            holder.binding.tvTitle.text=article.title
            holder.binding.tvDescription.text=article.description
            holder.binding.tvPublishedAt.text=article.publishedAt
            setOnClickListener{
                onItemClickListener?.let {
                    it(article)
                }
            }
        }
    }
    private var onItemClickListener:((Article)->Unit)?=null
    fun setOnItemClickListener(listener: (Article)->Unit){
        onItemClickListener=listener
    }
}