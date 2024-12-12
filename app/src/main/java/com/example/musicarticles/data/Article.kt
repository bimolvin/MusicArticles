package com.example.musicarticles.data

import androidx.annotation.DrawableRes
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class Article (
    @Json(name = "id")
    val id: Long,

    @DrawableRes
    @Json(name = "cover")
    val cover: Int?,

    @Json(name = "title")
    var title: String,

    @Json(name = "author")
    val author: String,

    @Json(name = "date")
    val date: Date,

    @Json(name = "content")
    var content: String
)
