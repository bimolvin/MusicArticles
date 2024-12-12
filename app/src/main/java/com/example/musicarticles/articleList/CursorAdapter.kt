package com.example.musicarticles.articleList

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.musicarticles.R

class CursorAdapter(
    private val context: Context,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<CursorAdapter.ArticleViewHolder>(){
    private var cursor: Cursor? = null

    /* List item click listener interface. */
    interface OnItemClickListener {
        fun onItemClick(id: Long)
    }

    /* Set new cursor. */
    fun swapCursor(newCursor: Cursor?) {
        if (cursor != null) {
            cursor?.close()
        }
        cursor = newCursor
        notifyDataSetChanged()
    }

    /* ViewHolder for a single list element. */
    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val author: TextView = itemView.findViewById(R.id.author)
        val date: TextView = itemView.findViewById(R.id.publish_date)
        val cover: ImageView = itemView.findViewById(R.id.cover)

        fun bind(curr: Cursor) {
            title.text = curr.getString(curr.getColumnIndexOrThrow("title"))
            author.text = curr.getString(curr.getColumnIndexOrThrow("author"))
            date.text = curr.getString(curr.getColumnIndexOrThrow("date"))

            val currCover = curr.getInt(curr.getColumnIndexOrThrow("cover"))
            if(currCover != 0) {
                cover.setImageResource(currCover)
            } else {
                cover.isVisible = false
            }
        }
    }

    /* Create new views (invoked by the layout manager). */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item, parent, false)
        return ArticleViewHolder(view)
    }

    /* Replace the contents of a view (invoked by the layout manager). */
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        cursor?.let {
            if (it.moveToPosition(position)) {
                holder.bind(it)
                val id = it.getLong(it.getColumnIndexOrThrow(BaseColumns._ID))

                /* Define click listener for the ViewHolder's View. */
                holder.itemView.setOnClickListener {
                    itemClickListener.onItemClick(id)  // pass id of clicked item
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return cursor?.count ?: 0
    }
}
