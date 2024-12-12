package com.example.musicarticles.json

import android.util.Log
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter


fun readJSONFromAssets(file: File): String {
    return try {
        val bufferedReader = BufferedReader(FileReader(file))
        val stringBuilder = StringBuilder()
        bufferedReader.useLines { lines ->
            lines.forEach {
                stringBuilder.append(it)
            }
        }
        stringBuilder.toString()
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

fun appendJSONToAssets(file: File, jsonData: String) {
    Log.i("Append", jsonData)
    var writer: BufferedWriter? = null
    try {
        writer = BufferedWriter(FileWriter(file))
        writer.write(jsonData)
        writer.close()
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        writer?.close()
    }
}
