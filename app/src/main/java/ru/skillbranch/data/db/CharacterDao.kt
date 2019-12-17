package ru.skillbranch.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverters
import ru.skillbranch.data.converters.DataConverter
import ru.skillbranch.data.local.entities.Character
import ru.skillbranch.data.local.entities.CharacterFull
import ru.skillbranch.data.local.entities.CharacterItem
import ru.skillbranch.data.local.entities.House


/**
 * @author neestell on 2019-12-12.
 */

@Dao
interface CharacterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @TypeConverters(DataConverter::class)
    suspend fun insertAll(houses: List<Character>)

    @Query("SELECT * FROM character WHERE houseId = :name")
    suspend fun getCharactersByHouseId(name: String): List<Character>

    @Query("SELECT * FROM character WHERE id = :id")
    suspend fun getCharactersByCharacterId(id: String): Character
}