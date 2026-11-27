package com.example.paraules.data

const val MIN_WORDS = 2
const val MAX_WORDS = 10

data class WordCategory(val name: String, val words: List<String>)