package com.example.musicarticles.articleList

import android.database.Cursor
import android.provider.BaseColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.example.musicarticles.R
import com.example.musicarticles.data.Article
import com.example.musicarticles.json.getSimpleDateFormat

class ArticlesAdapter(private val onClick: (Article) -> Unit) :
    ListAdapter<Article, ArticlesAdapter.ViewHolder>(ArticleDiffCallback) {

    class ViewHolder(view: View, onClick: (Article) -> Unit) : RecyclerView.ViewHolder(view) {
        private var cover: ImageView = view.findViewById(R.id.cover)
        private val title: TextView = view.findViewById(R.id.title)
        private val author: TextView = view.findViewById(R.id.author)
        private val date: TextView = view.findViewById(R.id.publish_date)
        private var currentArticle: Article? = null

        init {
            /* Define click listener for the ViewHolder's View. */
            itemView.setOnClickListener {
                currentArticle?.let {
                    onClick(it)
                }
            }
        }

        fun bind(article: Article) {
            currentArticle = article

            title.text = article.title
            author.text = article.author
            date.text = getSimpleDateFormat().format(article.date)

            if(article.cover != null) {
                cover.setImageResource(article.cover)
            } else {
                cover.isVisible = false
            }
        }
    }

    /* Create new views (invoked by the layout manager). */
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recyclerview_item, viewGroup, false)

        return ViewHolder(view, onClick)
    }

    /* Replace the contents of a view (invoked by the layout manager). */
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val article = getItem(position)
        viewHolder.bind(article)
    }

}

object ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.id == newItem.id
    }
}
