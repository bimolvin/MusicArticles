package com.example.musicarticles.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.musicarticles.json.getArticleListFromFile
import com.example.musicarticles.json.pushArticleListToFile
import java.io.File

/* Handles operations on articlesLiveData and holds details about it. */
class DataSource(private val file: File) {
    private val initialArticleList: List<Article> = getArticleListFromFile(file)
    private val articlesLiveData = MutableLiveData(initialArticleList)

    /* Save last removed article in order to push it back if needed. */
    private var lastRemoved : Article? = null

    /* Returns list of all articles. */
    fun getArticleList(): LiveData<List<Article>> {
        return articlesLiveData
    }

    /* Returns article given an ID. */
    fun getArticleById(id: Long): Article? {
        articlesLiveData.value?.let { articles ->
            return articles.firstOrNull{ it.id == id}
        }
        return null
    }

    /* Returns position at liveData given an article. */
    fun getArticleIndex(article: Article) : Int {
        articlesLiveData.value?.let { articles ->
            return articles.indexOf(article)
        }
        return -1
    }

    /* Adds article to liveData and posts value. */
    fun addArticle(article: Article, index: Int = 0) {
        val currentList = articlesLiveData.value
        if (currentList == null) {
            val updatedList = listOf(article)
            articlesLiveData.postValue(updatedList)
            pushArticleListToFile(file, updatedList)
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(index, article)
            articlesLiveData.postValue(updatedList)
            pushArticleListToFile(file, updatedList)
        }
    }

    /* Edits existing article at liveData and posts value. */
    fun editArticle(article: Article) {
        val currentList = articlesLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList[getArticleIndex(article)] = article
            articlesLiveData.postValue(updatedList)
            pushArticleListToFile(file, updatedList)
        }
    }

    /* Removes article from liveData and posts value. */
    fun removeArticle(article: Article) {
        val currentList = articlesLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            lastRemoved = article
            updatedList.remove(article)
            articlesLiveData.postValue(updatedList)
            pushArticleListToFile(file, updatedList)
        }
    }

    /* Adds last removed article to given position at liveData and posts value. */
    fun unRemoveArticle(index: Int) {
        lastRemoved?.let {
            addArticle(lastRemoved!!, index)
        }
    }

    companion object {
        private var INSTANCE: DataSource? = null

        fun getDataSource(file: File): DataSource {
            return synchronized(DataSource::class) {
                val newInstance = INSTANCE ?: DataSource(file)
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}

