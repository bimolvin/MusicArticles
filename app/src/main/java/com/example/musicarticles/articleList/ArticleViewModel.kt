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

class ArticleViewModel(application: Application) : AndroidViewModel(application) {

    private val _articleCursor = MutableLiveData<Cursor?>()
    val articleCursor: LiveData<Cursor?> = _articleCursor

    private val contentResolver = application.contentResolver

    // Метод для получения данных из ContentProvider
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

    fun addNewArticle(title: String?, content: String?, author: String) : Boolean {
        if (title == null || content == null) {
            return false
        }

        val contentValues = ContentValues().apply {
            put("title", title)
            put("author", author)
            put("content", content)
        }

        val newArticleUri: Uri? = contentResolver.insert(
            ArticlesContentProvider.CONTENT_URI, // URI провайдера
            contentValues  // Данные для вставки
        )

        return newArticleUri != null
    }

    fun removeArticle(articleId: Long) : Boolean {
        val articleUri = Uri.withAppendedPath(
            ArticlesContentProvider.CONTENT_URI,
            articleId.toString() // Конкретный URI для удаляемой статьи
        )

        val rowsDeleted = contentResolver.delete(
            articleUri, // URI статьи
            null, // Условие для удаления (здесь не нужно)
            null  // Аргументы для условия
        )

        return rowsDeleted > 0
    }

    fun unRemoveArticle(){
        // Определяем URI для вызова пользовательской функции
        val uri = Uri.parse("content://com.example.art-music/articles")

        val result = contentResolver.call(uri, "unRemoveArticle", null, null)

        if(!result?.getString("articleCount").toBoolean()) {
            Log.e("unRemoveArticle", "Error, the article is not unRemoved")
        }
    }

    // Метод для закрытия курсора, когда он больше не нужен
    fun closeCursor() {
        _articleCursor.value?.close()
    }
}
