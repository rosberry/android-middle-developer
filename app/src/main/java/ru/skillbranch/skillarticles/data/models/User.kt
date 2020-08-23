package ru.skillbranch.skillarticles.data.models

//@JsonClass(generateAdapter = true)
data class User(
        val id: String,
        val name: String,
        val avatar: String,
        val rating: Int = 0,
        val respect: Int = 0,
        val about: String = ""
)