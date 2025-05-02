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

    @SerialName(value = "genre_ids")
    val genreIds: List<Int> = emptyList(),

    @SerialName(value = "genres")
    val genres: List<Genre> = emptyList(),

    @SerialName(value = "homepage")
    val homepage: String? = null,

    @SerialName(value = "imdb_id")
    val imdbId: String? = null
)

@Serializable
data class Genre(
    val id: Int,
    val name: String
)

fun Movie.getGenreNames(): List<String> {
    return if (genres.isNotEmpty()) {
        genres.map { it.name }
    } else {
        getGenreNamesFromIds(genreIds)
    }
}

fun getGenreNamesFromIds(genreIds: List<Int>): List<String> {
    val genreMap = mapOf(
        28 to "Action",
        12 to "Adventure",
        16 to "Animation",
        35 to "Comedy",
        80 to "Crime",
        99 to "Documentary",
        18 to "Drama",
        10751 to "Family",
        14 to "Fantasy",
        36 to "History",
        27 to "Horror",
        10402 to "Music",
        9648 to "Mystery",
        10749 to "Romance",
        878 to "Science Fiction",
        10770 to "TV Movie",
        53 to "Thriller",
        10752 to "War",
        37 to "Western"
    )
    return genreIds.mapNotNull { genreMap[it] }
}