package ru.skillbranch.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.skillbranch.data.converters.DataConverter

@Entity
data class House(
        @ColumnInfo(name = "id") val id: String,
        @PrimaryKey val name: String,
        @ColumnInfo(name = "region") val region: String,
        @ColumnInfo(name = "coatOfArms") val coatOfArms: String,
        @ColumnInfo(name = "words") val words: String,
        @TypeConverters(DataConverter::class)
        @ColumnInfo(name = "titles") val titles: List<String>,
        @TypeConverters(DataConverter::class)
        @ColumnInfo(name = "seats") val seats: List<String>,
        @ColumnInfo(name = "currentLord") val currentLord: String, //rel
        @ColumnInfo(name = "heir") val heir: String, //rel
        @ColumnInfo(name = "overlord") val overlord: String,
        @ColumnInfo(name = "founded") val founded: String,
        @ColumnInfo(name = "founder") val founder: String, //rel
        @ColumnInfo(name = "diedOut") val diedOut: String,
        @TypeConverters(DataConverter::class)
        @ColumnInfo(name = "ancestralWeapons") val ancestralWeapons: List<String>,
        @ColumnInfo(name = "swornMembers") val swornMembers: List<String>


)