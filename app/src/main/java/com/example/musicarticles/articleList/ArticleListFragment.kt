package com.example.musicarticles.articleList

import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.musicarticles.data.Article
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicarticles.FILE_NAME
import com.example.musicarticles.R
import com.example.musicarticles.articleDetail.ARTICLE_INDEX
import com.example.musicarticles.articleDetail.ArticleDetailActivity
import com.example.musicarticles.data.ArticlesContentProvider
import com.google.android.material.snackbar.Snackbar
import java.io.File

const val ARTICLE_ID = "article id"

/* Displays list of all articles. */
class ArticleListFragment : Fragment(), CursorAdapter.OnItemClickListener {
    private val articlesListViewModel by viewModels<ArticleListViewModel> {
        ArticlesListViewModelFactory(
            File(
                (activity as AppCompatActivity).filesDir,
                FILE_NAME
            )
        )
    }
    private val articleViewModel: ArticleViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_article_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* Forming recycler view. */
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        /* Instantiates headerAdapter and articlesAdapter. Both adapters are added to concatAdapter.
        which displays the contents sequentially */
        val headerAdapter = HeaderAdapter()
//        val articlesAdapter = ArticlesAdapter { article -> adapterOnClick(article) }
        val articlesAdapter = CursorAdapter((activity as AppCompatActivity), this)
        val concatAdapter = ConcatAdapter(headerAdapter, articlesAdapter)
        recyclerView.adapter = concatAdapter

        articleViewModel.articleCursor.observe(viewLifecycleOwner) { cursor ->
            cursor?.let {
                // Обновляем адаптер с новым курсором
                articlesAdapter.swapCursor(it)
                headerAdapter.updateArticleCount(it.count)
            }
        }

        // Загружаем данные
        articleViewModel.fetchArticles()

//        articlesListViewModel.articleLiveData.observe(viewLifecycleOwner) {
//            it?.let {
//                articlesAdapter.submitList(it as MutableList<Article>)
//                headerAdapter.updateArticleCount(it.size)
//            }
//        }
    }

    /* Opens ArticleDetailActivity when RecyclerView item is clicked. */
    private fun adapterOnClick(article: Article) {
        val intent = Intent(activity, ArticleDetailActivity()::class.java)
        intent.putExtra(ARTICLE_ID, article.id)
        launcher.launch(intent)
    }

    /* Expects ArticleDetailActivity to return article to delete. */
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            view?.let {
                Snackbar.make(
                    it, R.string.message_remove_success, Snackbar.LENGTH_LONG)
                    .setAction(R.string.message_remove_undo) {
                        val data = result.data

                        data?.let { intentData ->
                            val articleIndex = intentData.getIntExtra(ARTICLE_INDEX, -1)
//                            articlesListViewModel.unRemoveArticle(articleIndex)
                            articleViewModel.unRemoveArticle()
                        }
                    }
                    .show()
            }
        }
    }
    override fun onItemClick(id: Long) {
        val intent = Intent(activity, ArticleDetailActivity()::class.java)
        intent.putExtra(ARTICLE_ID, id)
        launcher.launch(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        articleViewModel.closeCursor()  // Закрываем курсор, если активность уничтожается
    }

    companion object {
        @JvmStatic
        fun newInstance() = ArticleListFragment()
    }
}
