package com.example.moviedb2025.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.common.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromList(genres: List<Int>): String {
        return Gson().toJson(genres)
    }

    @TypeConverter
    fun toList(genresString: String): List<Int> {
        val type = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(genresString, type)
    }
}

