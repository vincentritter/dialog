package com.dialogapp.dialog.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dialogapp.dialog.model.Post

@Dao
abstract class PostsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertPosts(posts: List<Post>)

    @Query("SELECT * FROM posts WHERE belongsToEndpoint = :endpoint ORDER BY datetime(datePublished) DESC")
    abstract fun loadPostsByEndpoint(endpoint: String): DataSource.Factory<Int, Post>

    @Query("DELETE FROM posts WHERE belongsToEndpoint = :endpoint")
    abstract fun deletePostsByEndpoint(endpoint: String)

    @Query("DELETE FROM posts")
    abstract fun clear()
}
