package com.example.musicarticles.data

import android.content.res.Resources
import com.example.musicarticles.R
import java.util.Calendar
import com.example.musicarticles.json.getSimpleDateFormat

/* Initial data for testing. */
fun articleList(resources: Resources): List<Article> {
    return listOf(
        Article(
            id = 1,
            title = resources.getString(R.string.article1_title),
            cover = R.drawable.cover1,
            author = resources.getString(R.string.article1_author),
            date = getSimpleDateFormat().parse(resources.getString(R.string.article1_date))
                ?: Calendar.getInstance().time,
            content = resources.getString(R.string.article1_content)
        ),
        Article(
            id = 2,
            title = resources.getString(R.string.article2_title),
            cover = null,
            author = resources.getString(R.string.article2_author),
            date = getSimpleDateFormat().parse(resources.getString(R.string.article2_date))
                ?: Calendar.getInstance().time,
            content = resources.getString(R.string.article2_content)
        ),
        Article(
            id = 3,
            title = resources.getString(R.string.article3_title),
            cover = R.drawable.cover3,
            author = resources.getString(R.string.article3_author),
            date = getSimpleDateFormat().parse(resources.getString(R.string.article3_date))
                ?: Calendar.getInstance().time,
            content = resources.getString(R.string.article3_content)
        ),
        Article(
            id = 4,
            title = resources.getString(R.string.article4_title),
            cover = R.drawable.cover4,
            author = resources.getString(R.string.article4_author),
            date = getSimpleDateFormat().parse(resources.getString(R.string.article4_date))
                ?: Calendar.getInstance().time,
            content = resources.getString(R.string.article4_content)
        ),
    )
}