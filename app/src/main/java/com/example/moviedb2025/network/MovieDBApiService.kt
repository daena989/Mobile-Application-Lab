package com.example.moviedb2025.network

//Should have more lines of code here but cannot see in photos
import com.example.moviedb2025.models.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieDBApiService {

    @GET("popular")
    suspend fun getPopularMovies(
        @Query("api_key")
        apikey: String = Constants.API.KEY
    ): MovieResponse

    @GET("top-rated")
    suspend fun getTopRatedMovies(
        @Query("api_key")
        apikey: String = Constants.API.KEY
    ): MovieResponse
}