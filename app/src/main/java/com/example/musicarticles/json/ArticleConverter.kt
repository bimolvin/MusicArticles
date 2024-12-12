package com.example.musicarticles.json

import android.util.Log
import com.example.musicarticles.data.Article
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.File
import java.lang.reflect.Type


fun getArticleListFromFile(file: File): List<Article> {
    val data: String = readJSONFromAssets(file)
    var articles = emptyList<Article>()

    val adapterResult = jsonAdapterForList().fromJson(data)
    adapterResult?.let {
        articles = adapterResult
    }
    return articles
}

fun pushArticleListToFile(file: File, articles: List<Article>) {
    val json: String = jsonAdapterForList().toJson(articles)
    appendJSONToAssets(file, json)
    Log.i("Moshi", json)
}

private fun jsonAdapterForList(): JsonAdapter<List<Article>> {
    val moshi: Moshi = Moshi.Builder()
        .add(DateTimeAdapter)
        .build()
    val listOfArticlesType: Type = Types.newParameterizedType(
        List::class.java,
        Article::class.java
    )
    return moshi.adapter(listOfArticlesType)
}
