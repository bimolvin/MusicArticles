package com.example.musicarticles.articleDetail

import android.app.Application
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.musicarticles.data.Article
import com.example.musicarticles.data.ArticlesContentProvider
import com.example.musicarticles.data.DataSource
import com.example.musicarticles.json.getSimpleDateFormat
import kotlinx.coroutines.launch
import java.io.File
import java.net.URI
import java.text.SimpleDateFormat

class CursorArticleDetailViewModel(application: Application) : AndroidViewModel(application) {

    var article: Article? = null
    private val contentResolver = application.contentResolver

    // Метод для получения данных из ContentProvider
    fun fetchArticle(articleUri: Uri) : Article? {
        Log.i("fetchArticle", "here!")
        viewModelScope.launch {
            // Выполняем запрос с помощью ContentResolver
            val cursor: Cursor? = contentResolver.query(
                articleUri,  // URI для запроса
                null,  // Проекция (все столбцы)
                null,  // Условие выбора (по id все будет отфильтровано через URI)
                null,  // Аргументы для selection
                null   // Порядок сортировки
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

    fun updateArticle(article: Article) : Boolean {
        val contentValues = ContentValues().apply {
            put("title", article.title)
            put("content", article.content)
        }

        val articleUri = Uri.withAppendedPath(
            ArticlesContentProvider.CONTENT_URI,
            article.id.toString() // Конкретный URI для обновляемой статьи
        )

        val rowsUpdated = contentResolver.update(
            articleUri, // URI статьи
            contentValues, // Новые данные
            null, // Условие для выбора строки (здесь не нужно)
            null  // Аргументы для условия
        )

        return rowsUpdated > 0
    }
}

class ArticleDetailViewModel(private val dataSource: DataSource) : ViewModel() {
    /* Queries datasource to return an article that corresponds to an id. */
    fun getArticleById(id: Long) : Article? {
        return dataSource.getArticleById(id)
    }

    /* Queries datasource to return an index that corresponds to an article id. */
    fun getArticleIndex(article: Article) : Int {
        return dataSource.getArticleIndex(article)
    }

    /* Queries datasource to edit an article. */
    fun editArticle(article: Article) {
        dataSource.editArticle(article)
    }

    /* Queries datasource to remove an article. */
    fun removeArticle(article: Article) {
        dataSource.removeArticle(article)
    }
}

class ArticleDetailViewModelFactory(private val file: File) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArticleDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ArticleDetailViewModel(
                dataSource = DataSource.getDataSource(file)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}