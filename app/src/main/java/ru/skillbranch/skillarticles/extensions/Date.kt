package ru.skillbranch.skillarticles.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}