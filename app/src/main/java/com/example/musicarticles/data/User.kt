package com.example.musicarticles.data

import com.example.musicarticles.json.getSimpleDateFormat
import java.util.Date

object User {
    var userLastUpdate: Date? = getSimpleDateFormat()
        .parse("08 декабря 2024")
}