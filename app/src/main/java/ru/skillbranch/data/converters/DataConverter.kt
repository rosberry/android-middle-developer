package ru.skillbranch.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


/**
 * @author neestell on 2019-12-12.
 */
class DataConverter {
    @TypeConverter
    fun fromCountryLangList(list: List<String>?): String? {
        if(list == null) return null
        val gson = Gson()
        val type: Type = object : TypeToken<List<String>>() {}.type
        return gson.toJson(list, type)
    }

    @TypeConverter
    fun toCountryLangList(listJson: String?): List<String>? {
        if (listJson == null) return listOf()
        val gson = Gson()
        val type: Type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(listJson, type)
    }
}