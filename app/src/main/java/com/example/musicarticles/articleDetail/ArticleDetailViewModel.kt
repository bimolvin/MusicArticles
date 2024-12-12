package com.example.musicarticles.articleDetail

import android.app.Application
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicarticles.data.Article
import com.example.musicarticles.data.ArticlesContentProvider
import com.example.musicarticles.json.getSimpleDateFormat
import kotlinx.coroutines.launch

class ArticleDetailViewModel(application: Application) : AndroidViewModel(application) {

    var article: Article? = null
    private val contentResolver = application.contentResolver

    /* Getting data from ContentProvider. */
    fun fetchArticle(articleUri: Uri) : Article? {
        Log.i("fetchArticle", "here!")
        viewModelScope.launch {
            // Using ContentResolver to send request
            val cursor: Cursor? = contentResolver.query(
                articleUri,
                null,
                null,
                null,
                null
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    val id = it.getLong(it.getColumnIndexOrThrow(BaseColumns._ID))
                    var cover: Int? = it.getInt(it.getColumnIndexOrThrow("cover"))
                    val title = it.getString(it.getColumnIndexOrThrow("title"))
                    val author = it.getString(it.getColumnIndexOrThrow("author"))
                    val date = it.getString(it.getColumnIndexOrThrow("date"))
                    val content = it.getString(it.getColumnIndexOrThrow("content"))

                    if(cover == 0) {
                        cover = null
                    }

                    article = getSimpleDateFormat().parse(date)
                        ?.let { parsedDate -> Article(id, cover, title, author, parsedDate, content) }
                }
            }
        }
        return article
    }

    /* Updating data in ContentProvider given an article. */
    fun updateArticle(article: Article) : Boolean {
        val contentValues = ContentValues().apply {
            put("title", article.title)
            put("content", article.content)
        }

        val articleUri = Uri.withAppendedPath(
            ArticlesContentProvider.CONTENT_URI,
            article.id.toString() // URI of article to update
        )

        val rowsUpdated = contentResolver.update(
            articleUri,
            contentValues, // new data
            null,
            null
        )

        return rowsUpdated > 0
    }
}
