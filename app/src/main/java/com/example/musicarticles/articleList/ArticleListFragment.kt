package com.example.musicarticles.articleList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicarticles.R
import com.example.musicarticles.articleDetail.ArticleDetailActivity
import com.google.android.material.snackbar.Snackbar

const val ARTICLE_ID = "article id"

/* Displays list of all articles. */
class ArticleListFragment : Fragment(), CursorAdapter.OnItemClickListener {
    private val articleListViewModel: ArticleListViewModel by viewModels()

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
        val articlesAdapter = CursorAdapter((activity as AppCompatActivity), this)
        val concatAdapter = ConcatAdapter(headerAdapter, articlesAdapter)
        recyclerView.adapter = concatAdapter

        articleListViewModel.articleCursor.observe(viewLifecycleOwner) { cursor ->
            cursor?.let {
                // Updating adapter with new cursor
                articlesAdapter.swapCursor(it)

                // Updating number of articles in header
                headerAdapter.updateArticleCount(it.count)
            }
        }

        /* Loading data */
        articleListViewModel.fetchArticles()
    }
    override fun onItemClick(id: Long) {
        val intent = Intent(activity, ArticleDetailActivity()::class.java)
        intent.putExtra(ARTICLE_ID, id)
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
                        articleListViewModel.unRemoveArticle()
                    }
                    .show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        articleListViewModel.closeCursor()  // Close cursor if activity has been destroyed
    }

    companion object {
        @JvmStatic
        fun newInstance() = ArticleListFragment()
    }
}
