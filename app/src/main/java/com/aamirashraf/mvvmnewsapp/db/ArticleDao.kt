package com.aamirashraf.mvvmnewsapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aamirashraf.mvvmnewsapp.models.Article

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article):Long
    @Delete
    suspend fun delete(article: Article)

    //for query related stuff
    @Query("select *from articles")
    fun getAllArticles():LiveData<List<Article>>


}