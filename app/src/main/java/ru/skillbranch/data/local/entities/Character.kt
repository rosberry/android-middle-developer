package ru.skillbranch.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Character(
        @PrimaryKey val id: String,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "gender") val gender: String,
        @ColumnInfo(name = "culture") val culture: String,
        @ColumnInfo(name = "born") val born: String,
        @ColumnInfo(name = "died") val died: String,
        @ColumnInfo(name = "titles") val titles: List<String> = listOf(),
        @ColumnInfo(name = "aliases") val aliases: List<String> = listOf(),
        @ColumnInfo(name = "father")val father: String, //rel
        @ColumnInfo(name = "mother") val mother: String, //rel
        @ColumnInfo(name = "spouse") val spouse: String,
        @ColumnInfo(name = "houseId") val houseId: String//rel
)

data class CharacterItem(
    val id: String,
    val house: String, //rel
    val name: String,
    val titles: List<String>,
    val aliases: List<String>
)

data class CharacterFull(
    val id: String,
    val name: String,
    val words: String, // house
    val born: String,
    val died: String,
    val titles: List<String>,
    val aliases: List<String>,
    val house:String, //rel load
    val father: RelativeCharacter?,
    val mother: RelativeCharacter?
)

data class RelativeCharacter(
    val id: String,
    val name: String,
    val house:String //rel
)