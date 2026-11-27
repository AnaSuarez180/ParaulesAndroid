package com.example.paraules.data

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import org.json.JSONObject

fun loadCategories(context: Context): List<WordCategory> {
    val json = context.assets.open("substantius_300_ca.json")
        .bufferedReader(Charsets.UTF_8)
        .use { it.readText() }
    val objectJson = JSONObject(json)
    return objectJson.keys().asSequence().map { category ->
        val words = objectJson.getJSONArray(category)
        WordCategory(category, List(words.length()) { index -> words.getString(index) })
    }.toList()
}

fun copyPhrase(context: Context, words: List<String>) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("Paraules", phraseFor(words)))
}

fun phraseFor(words: List<String>): String =
    words.joinToString(" ").replaceFirstChar { it.uppercase() } + "."