package ru.skillbranch.skillarticles.data

import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.Date

/**
 * @author mmikhailov on 23.08.2020.
 */
object JsonConverter {

    val moshi = Moshi.Builder()
        .add(DateAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    class DateAdapter {
        @FromJson
        fun fromJson(timestamp: Long) = Date(timestamp)

        @ToJson
        fun toJson(date: Date) = date.time
    }
}