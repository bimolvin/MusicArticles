package com.example.musicarticles.articleDetail

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.example.musicarticles.articleList.ARTICLE_ID
import com.example.musicarticles.FILE_NAME
import com.example.musicarticles.MainActivity
import com.example.musicarticles.R
import com.example.musicarticles.articleList.ArticleViewModel
import com.example.musicarticles.data.Article
import com.example.musicarticles.editor.ARTICLE_TO_EDIT_ID
import com.example.musicarticles.editor.EditorFragment
import com.example.musicarticles.json.getSimpleDateFormat
import com.google.android.material.snackbar.Snackbar
import java.io.File

const val ARTICLE_INDEX = "article index"

/* Displays article chosen from list. */
class ArticleDetailActivity : AppCompatActivity(), ArticleActionsBottomSheet.DialogListener {
    private val articleDetailViewModel by viewModels<ArticleDetailViewModel> {
        ArticleDetailViewModelFactory(
            File(
                filesDir,
                FILE_NAME
            )
        )
    }
    private val articleListViewModel: ArticleViewModel by viewModels()
    private val articleViewModel: CursorArticleDetailViewModel by viewModels()
    private lateinit var articleUri: Uri

    private lateinit var articleActionsBottomSheet: ArticleActionsBottomSheet
    private val listener: ArticleActionsBottomSheet.DialogListener = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)

        var currentArticleId: Long? = null

        /* Connect variables to UI elements. */
        val cover: ImageView = findViewById(R.id.cover)
        val title: TextView = findViewById(R.id.title)
        val author: TextView = findViewById(R.id.author)
        val date: TextView = findViewById(R.id.publish_date)
        val content: TextView = findViewById(R.id.text_content)

        val buttonBack : Button = findViewById(R.id.button_back)
        val buttonMore : Button = findViewById(R.id.button_more)
        val error: TextView = findViewById(R.id.error)

        val bundle: Bundle? = intent.extras
        bundle?.let {
            currentArticleId = bundle.getLong(ARTICLE_ID)
        }

        /* If currentArticleId is not null, get corresponding article and set title, image and
        content */
        currentArticleId?.let {
            // Формируем URI для запроса по id
            articleUri = Uri.parse("content://com.example.art-music/articles/$currentArticleId")
            val currentArticle = articleViewModel.fetchArticle(articleUri)
//            val currentArticle = articleDetailViewModel.getArticleById(it)
            if(currentArticle != null) {
                title.text = currentArticle.title
                author.text = currentArticle.author
                date.text = getSimpleDateFormat().format(currentArticle.date)
                content.text = currentArticle.content

                currentArticle.cover?.let {
                    cover.setImageResource(currentArticle.cover)
                }

                /* Go back to article list. */
                buttonBack.setOnClickListener {_ ->
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }

                /* Open menu with extra actions. */
                buttonMore.setOnClickListener {_ ->
                    articleActionsBottomSheet = ArticleActionsBottomSheet(listener, currentArticle)
                    articleActionsBottomSheet.show(supportFragmentManager, ArticleActionsBottomSheet.TAG)
                }
            }
            else {
                error.isVisible = true
                buttonMore.isVisible = false
            }
        }
    }

    override fun copyArticleLink(article: Article) {
        Log.i("Detail", "copy link!")
        Snackbar.make(findViewById(R.id.article_detail), R.string.message_copied_link,
            Snackbar.LENGTH_SHORT).show()
        articleActionsBottomSheet.dismiss()
    }

    /* Redirects to editor page and fills title and content fields with current article's data. */
    override fun editArticle(article: Article) {
        Log.i("Detail", "edit!")
        articleActionsBottomSheet.dismiss()

        val transaction = supportFragmentManager.beginTransaction()
        val fragment = EditorFragment()
        fragment.arguments = bundleOf(
            ARTICLE_TO_EDIT_ID to article.id.toString()
        )
        transaction.replace(R.id.article_detail, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun changeArticleCover(article: Article) {
        Log.i("Detail", "change cover!")
    }

    /* Removes current article from list and redirects to main page. */
    override fun deleteArticle(article: Article) {
        Log.i("Detail", "delete!")
        val intent = Intent(this, MainActivity::class.java)

        /* save list index of article to remove in order to restore it if needed. */
        val saveIndex = articleDetailViewModel.getArticleIndex(article)
        if(saveIndex == -1) {
            this.setResult(Activity.RESULT_CANCELED, intent)
        } else {
//            articleDetailViewModel.removeArticle(article)
            val removed = articleListViewModel.removeArticle(article.id)
            intent.putExtra(ARTICLE_INDEX, saveIndex)
            if(removed) this.setResult(Activity.RESULT_OK, intent) else this.setResult(Activity.RESULT_CANCELED, intent)
        }
        this.finish()
    }
}