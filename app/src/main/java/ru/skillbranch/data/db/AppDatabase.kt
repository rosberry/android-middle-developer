package ru.skillbranch.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.skillbranch.data.converters.DataConverter
import ru.skillbranch.data.local.entities.House
import ru.skillbranch.data.local.entities.Character


/**
 * @author neestell on 2019-12-12.
 */
@Database(entities = arrayOf(House::class, Character::class), version = 1, exportSchema = false)
@TypeConverters(DataConverter::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private var db: AppDatabase? = null

        suspend fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                if (db == null)
                    db = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java, "gt-db"
                    )
                        .build()
                return db!!
            }
        }
    }

    abstract fun houseDao(): HouseDao

    abstract fun characterDao(): CharacterDao

}