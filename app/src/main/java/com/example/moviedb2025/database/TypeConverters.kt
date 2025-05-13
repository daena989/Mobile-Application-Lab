package com.example.moviedb2025.database

import androidx.room.TypeConverter
import com.example.moviedb2025.models.Genre
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject

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

    @TypeConverter
    fun fromGenreList(genres: List<Genre>): String {
        val jsonArray = JSONArray()
        genres.forEach { genre ->
            val json = JSONObject().apply {
                put("id", genre.id)
                put("name", genre.name)
            }
            jsonArray.put(json)
        }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toGenreList(genresString: String): List<Genre> {
        val jsonArray = JSONArray(genresString)
        val genres = mutableListOf<Genre>()
        for (i in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(i)
            genres.add(
                Genre(
                    id = json.getInt("id"),
                    name = json.getString("name")
                )
            )
        }
        return genres
    }
}

