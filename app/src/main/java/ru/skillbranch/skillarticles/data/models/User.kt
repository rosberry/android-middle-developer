package ru.skillbranch.skillarticles.data.models

import java.util.Date

//@JsonClass(generateAdapter = true)
data class User(
        val id: String,
        val name: String,
        val avatar: String,
        val rating: Int = 0,
        val respect: Int = 0,
        val about: String,
        val lastVisit: Date = Date(),
        val contacts: Map<String, String> = mapOf()
)