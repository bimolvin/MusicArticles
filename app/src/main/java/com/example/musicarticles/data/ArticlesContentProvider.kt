package com.example.musicarticles.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import com.example.musicarticles.FILE_NAME
import com.example.musicarticles.json.getArticleListFromFile
import com.example.musicarticles.json.getSimpleDateFormat
import com.example.musicarticles.json.pushArticleListToFile
import java.io.File
import java.util.*

class ArticlesContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.art-music"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/articles")

        const val ARTICLES = 1
        const val ARTICLE_ID = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "articles", ARTICLES)
            addURI(AUTHORITY, "articles/#", ARTICLE_ID)
        }
    }

    // for storing data locally
    private val articles = mutableListOf<Article>()
    private var lastRemoved : Article? = null
    private var lastRemovedIndex : Int? = null
    private lateinit var file: File

    override fun onCreate(): Boolean {
        /* Getting data from file. */
        context?.let { ctx ->
            try {
                file = File(ctx.filesDir, FILE_NAME)
                articles.addAll(getArticleListFromFile(file))
                Log.i("ArticlesContentProvider", "Success loading data from file")
            } catch (e: Exception) {
                Log.e("ArticlesContentProvider", "Error loading data from file", e)
            }
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val cursor = when (uriMatcher.match(uri)) {
            ARTICLES -> {
                /* Converting list to Cursor. */
                val matrixCursor = MatrixCursor(arrayOf(BaseColumns._ID, "cover", "title", "author", "date", "content"))
                articles.forEach { article ->
                    matrixCursor.addRow(arrayOf(article.id, article.cover, article.title, article.author, getSimpleDateFormat().format(article.date), article.content))
                }
                matrixCursor
            }
            ARTICLE_ID -> {
                val articleId = uri.lastPathSegment?.toLongOrNull()
                if (articleId != null) {
                    val article = articles.find { it.id == articleId }
                    if (article != null) {
                        val matrixCursor = MatrixCursor(arrayOf(BaseColumns._ID, "cover", "title", "author", "date", "content"))
                        matrixCursor.addRow(arrayOf(article.id, article.cover, article.title, article.author, getSimpleDateFormat().format(article.date), article.content))
                        matrixCursor
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
            else -> null
        }
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return when (uriMatcher.match(uri)) {
            ARTICLES -> {
                val article = Article(
                    id = values?.getAsLong("id") ?: System.currentTimeMillis(),
                    cover = values?.getAsInteger("cover"),
                    title = values?.getAsString("title") ?: "",
                    author = values?.getAsString("author") ?: "",
                    date = Date(),
                    content = values?.getAsString("content") ?: ""
                )
                articles.add(0, article)
                pushArticleListToFile(file, articles)
                Uri.withAppendedPath(CONTENT_URI, article.id.toString())
            }
            else -> null
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        when (uriMatcher.match(uri)) {
            ARTICLE_ID -> {
                val articleId = uri.lastPathSegment?.toLongOrNull()
                if (articleId != null) {
                    val articleIndex = articles.indexOfFirst { it.id == articleId }
                    if (articleIndex != -1) {
                        val article = articles[articleIndex]
                        val updatedArticle = article.copy(
                            title = values?.getAsString("title") ?: article.title,
                            content = values?.getAsString("content") ?: article.content
                        )
                        articles[articleIndex] = updatedArticle
                        pushArticleListToFile(file, articles)
                        return 1
                    }
                }
            }
        }
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        when (uriMatcher.match(uri)) {
            ARTICLE_ID -> {
                val articleId = uri.lastPathSegment?.toLongOrNull()
                if (articleId != null) {
                    val articleIndex = articles.indexOfFirst { it.id == articleId }
                    if (articleIndex != -1) {
                        lastRemoved = articles[articleIndex]
                        lastRemovedIndex = articleIndex
                        articles.removeAt(articleIndex)
                        pushArticleListToFile(file, articles)
                        return 1
                    }
                }
            }
        }
        return 0
    }

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        val bundle = Bundle()

        when (method) {
            "unRemoveArticle" -> {
                val unRemoved = unRemove()
                bundle.putBoolean("articleCount", unRemoved)
            }
            else -> {
                return super.call(method, arg, extras)  // Using standard call if method is not found
            }
        }
        return bundle
    }

    private fun unRemove() : Boolean {
        return if(lastRemoved != null && lastRemovedIndex != null) {
            articles.add(lastRemovedIndex!!, lastRemoved!!)
            pushArticleListToFile(file, articles)
            Uri.withAppendedPath(CONTENT_URI, lastRemoved!!.id.toString())
            true
        } else false
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            ARTICLES -> "vnd.android.cursor.dir/vnd.$AUTHORITY.articles"
            ARTICLE_ID -> "vnd.android.cursor.item/vnd.$AUTHORITY.articles"
            else -> null
        }
    }
}
