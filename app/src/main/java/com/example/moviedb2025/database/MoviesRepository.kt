package com.example.moviedb2025.database

import com.example.moviedb2025.models.MovieDBApiService

interface MoviesRepository {
    suspend fun getPopularMovies(): MovieResponse
    suspend fun getTopRatedMovies(): MovieResponse
}

class NetworkMoviesRepository (private val apiService: MovieDBApiService) : MovieRepository {
    override suspend fun getPopularMovies() : MovieResponse {
        return apiService.getPopularMovies()
    }

    override suspend fun getTopRatedMovies(): MovieResponse {
        return apiService.getTopRatedMovies()
    }
}