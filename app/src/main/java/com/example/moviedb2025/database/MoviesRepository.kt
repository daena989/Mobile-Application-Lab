package com.example.moviedb2025.database

import com.example.moviedb2025.models.Movie
import com.example.moviedb2025.models.MovieResponse
import com.example.moviedb2025.models.ReviewResponse
import com.example.moviedb2025.network.MovieDBApiService

interface MoviesRepository { // defines the methods for fetching movie data; abstracts data source allowing the rest of the app to interact with the movie data without knowing the details
    suspend fun getPopularMovies(): MovieResponse
    suspend fun getTopRatedMovies(): MovieResponse
    suspend fun getMovieDetails(movieId: Long): Movie
    suspend fun getMovieReviews(movieId: Long): ReviewResponse
}

// NetworkMR class implements the MovieRepository interface
class NetworkMoviesRepository(private val apiService: MovieDBApiService) : MoviesRepository { // Take an instance of MovieDBApiService as a constructor parameter to call diff methods in MovieDBApiService to fetch data and return as MovieResponse
    override suspend fun getPopularMovies(): MovieResponse {
        return apiService.getPopularMovies()
    }

    override suspend fun getTopRatedMovies(): MovieResponse {
        return apiService.getTopRatedMovies()
    }

    override suspend fun getMovieDetails(movieId: Long): Movie {
        return apiService.getMovieDetails(movieId)
    }

    override suspend fun getMovieReviews(movieId: Long): ReviewResponse { // NEW
        return apiService.getMovieReviews(movieId)
    }
}