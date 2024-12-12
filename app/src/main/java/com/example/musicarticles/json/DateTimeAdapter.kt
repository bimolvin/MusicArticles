package com.example.musicarticles.json

import android.net.ParseException
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

var DateTimeAdapter: Any = object : Any() {
    private val simpleDateFormat = getSimpleDateFormat()

    @ToJson
    @Synchronized
    fun dateToJson(d: Date): String {
        return simpleDateFormat.format(d)
    }

    @FromJson
    @Synchronized
    @Throws(ParseException::class)
    fun dateToJson(s: String): Date {
        return simpleDateFormat.parse(s) ?: Calendar.getInstance().time
    }
}

fun getSimpleDateFormat() : SimpleDateFormat{
    val pattern = "dd MMMM yyyy"
    return SimpleDateFormat(pattern, Locale("ru", "RU"))
}
