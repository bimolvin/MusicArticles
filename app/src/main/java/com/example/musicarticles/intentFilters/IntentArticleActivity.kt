package com.example.musicarticles.intentFilters

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.musicarticles.MainActivity
import com.example.musicarticles.articleDetail.ArticleDetailActivity
import com.example.musicarticles.articleList.ARTICLE_ID

class IntentArticleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent: Intent = intent
        val data: Uri? = intent.data

        data?.let {
            val articleId = it.lastPathSegment?.toLongOrNull()

            if (articleId != null) {
                redirectToArticle(articleId)
            } else {
                val intentMain = Intent(this, MainActivity::class.java)
                startActivity(intentMain)
            }
        }
    }

    private fun redirectToArticle(id: Long) {
        val intent = Intent(this, ArticleDetailActivity()::class.java)
        intent.putExtra(ARTICLE_ID, id)
        startActivity(intent)
    }
}