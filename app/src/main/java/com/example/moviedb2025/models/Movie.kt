package com.example.moviedb2025.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    @SerialName(value = "id")
    var id: Long = 0L,

    @SerialName(value = "title")
    var title: String,

    @SerialName(value = "poster_path")
    var posterPath: String,

    @SerialName(value = "backdrop_path")
    var backdropPath: String? = null,

    @SerialName(value = "release_date")
    var releaseDate: String,

    @SerialName(value = "overview")
    var overview: String,

    @SerialName(value = "genres")
    val genres: List<String> = emptyList(), // << changed

    @SerialName(value = "homepage")
    val homepage: String? = null, // << made nullable

    @SerialName(value = "imdb_id")
    val imdbId: String? = null // << made nullable
)
