package com.example.musicarticles.articleList

import android.app.Application
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicarticles.data.ArticlesContentProvider
import kotlinx.coroutines.launch

class ArticleListViewModel(application: Application) : AndroidViewModel(application) {

    private val _articleCursor = MutableLiveData<Cursor?>()
    val articleCursor: LiveData<Cursor?> = _articleCursor

    private val contentResolver = application.contentResolver

    /* Getting all articles from ContentProvider. */
    fun fetchArticles() {
        Log.i("fetchArticles", "here!")
        viewModelScope.launch {
            val cursor: Cursor? = contentResolver.query(
                ArticlesContentProvider.CONTENT_URI,
                null,
                null,
                null,
                null
            )

            _articleCursor.postValue(cursor)
        }
    }

    /* Adding new article to ContentProvider given id, title, content and author. */
    fun addNewArticle(id: Long, title: String?, content: String?, author: String) : Boolean {
        if (title == null || content == null) {
            return false
        }

        val contentValues = ContentValues().apply {
            put("id", id)
            put("title", title)
            put("author", author)
            put("content", content)
        }

        val newArticleUri: Uri? = contentResolver.insert(
            ArticlesContentProvider.CONTENT_URI, // provider URI
            contentValues  // new data
        )

        return newArticleUri != null
    }

    /* Removing article from ContentProvider given article id. */
    fun removeArticle(articleId: Long) : Boolean {
        val articleUri = Uri.withAppendedPath(
            ArticlesContentProvider.CONTENT_URI,
            articleId.toString() // URI of article to delete
        )

        val rowsDeleted = contentResolver.delete(
            articleUri,
            null,
            null
        )

        return rowsDeleted > 0
    }

    /* UNDO of removing article. */
    fun unRemoveArticle(){
        // Defining URI for calling custom function
        val uri = Uri.parse("content://com.example.art-music/articles")

        val result = contentResolver.call(uri, "unRemoveArticle", null, null)

        if(!result?.getString("articleCount").toBoolean()) {
            Log.e("unRemoveArticle", "Error, the article is not unRemoved")
        }
    }

    /* Closing cursor if not needed anymore. */
    fun closeCursor() {
        _articleCursor.value?.close()
    }
}
