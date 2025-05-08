package com.example.moviedb2025.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_movies")
data class CachedMovie(
    @PrimaryKey val id: Int,
    val adult: Boolean,
    val backdrop_path: String?,
    val genres: List<Genre>,
    val original_language: String,
    val original_title: String,
    val overview: String?,
    val popularity: Double,
    val poster_path: String?,
    val release_date: String,
    val title: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int,
    val viewType: String      // either "popular" or "top_rated"
)

fun CachedMovie.toMovie(): Movie {
    return Movie(
        id = id.toLong(),
        title = title,
        posterPath = poster_path ?: "",
        backdropPath = backdrop_path,
        releaseDate = release_date,
        overview = overview ?: "",
        genresIds = genres.map { it.id },
        homepage = null,
        imdbId = null,
        type = viewType
    )
}
fun Movie.toCached(viewType: String): CachedMovie {
    return CachedMovie(
        id = this.id.toInt(),
        adult = false, // If not available in Movie, assume false or change accordingly
        backdrop_path = this.backdropPath,
        genres = emptyList(), // You may need to fetch full genres separately
        original_language = "en", // Adjust as needed
        original_title = this.title,
        overview = this.overview,
        popularity = 0.0, // If not provided
        poster_path = this.posterPath,
        release_date = this.releaseDate,
        title = this.title,
        video = false,
        vote_average = 0.0, // Replace with real data if available
        vote_count = 0,
        viewType = viewType
    )
}