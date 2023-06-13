package com.aamirashraf.mvvmnewsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aamirashraf.mvvmnewsapp.models.Article

@Database(
    entities = [Article::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase:RoomDatabase() {
    abstract fun getArticleDao():ArticleDao
    companion object{
        @Volatile   //volatile means all thread now if there is any changes in the db
        private var INSTANCE:ArticleDatabase?=null
        fun createDatabase(context: Context):ArticleDatabase{
            if(INSTANCE==null){
                synchronized(this){
                    INSTANCE= Room.databaseBuilder(context.applicationContext,ArticleDatabase::class.java,"article_db.db").build()
                }

            }
            return INSTANCE!!
        }
    }
}