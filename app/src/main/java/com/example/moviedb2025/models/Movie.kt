package com.example.moviedb2025.models

data class Movie(
    var id: Long = 0L,
    var title: String,
    var posterPath: String,
    var backdropPath: String,
    var releaseDate: String,
    var overview: String,
    val genres: List<String>,
    val homepage: String,
    val imdbId: String

)