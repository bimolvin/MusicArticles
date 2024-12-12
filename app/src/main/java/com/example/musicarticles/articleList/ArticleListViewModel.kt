package com.example.musicarticles.articleList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicarticles.data.Article
import com.example.musicarticles.data.DataSource
import java.io.File
import java.util.Calendar
import kotlin.random.Random

class ArticleListViewModel(private val dataSource: DataSource) : ViewModel() {
    val articleLiveData = dataSource.getArticleList()

    /* Queries datasource to add an article given a formed article. */
    fun addArticle(article: Article) {
        dataSource.addArticle(article)
    }
    /* Queries datasource to add an article given a title, a content and the author (user name). */
    fun addArticle(title: String?, content: String?, author: String) {
        if (title == null || content == null) {
            return
        }

        val newArticle = Article(
            id = Random.nextLong(),
            title = title,
            cover = null,
            author = author,
            date = Calendar.getInstance().time,
            content = content
        )
        dataSource.addArticle(newArticle)
    }

    /* Queries datasource to remove an article that corresponds to an id. */
    fun removeArticle(articleId: Long) {
        val article = dataSource.getArticleById(articleId)
        if(article != null) {
            dataSource.removeArticle(article)
        }
    }

    /* Queries datasource to cancel removing an article and insert it at the given position. */
    fun unRemoveArticle(index: Int) {
        dataSource.unRemoveArticle(index)
    }
}

class ArticlesListViewModelFactory(private val file: File) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArticleListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ArticleListViewModel(
                dataSource = DataSource.getDataSource(file)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}