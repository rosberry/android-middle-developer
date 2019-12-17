package ru.skillbranch.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverters
import ru.skillbranch.data.converters.DataConverter
import ru.skillbranch.data.local.entities.House


/**
 * @author neestell on 2019-12-12.
 */

@Dao
interface HouseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @TypeConverters(DataConverter::class)
    suspend fun insertAll(houses: List<House>)

    @Query("SELECT * FROM house")
    suspend fun getAll(): List<House>

    @Query("SELECT * FROM house WHERE swornMembers LIKE '%' || :characterId  || '%'")
    suspend fun getHouseForCharacterId(characterId: String): House
}