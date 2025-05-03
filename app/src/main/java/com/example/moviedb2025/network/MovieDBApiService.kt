package com.example.moviedb2025.network

import android.provider.SyncStateContract
import com.example.moviedb2025.models.Movie
import com.example.moviedb2025.models.MovieResponse
import com.example.moviedb2025.models.ReviewResponse
import com.example.moviedb2025.models.VideoResponse
import com.example.moviedb2025.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieDBApiService {

    @GET("popular") // @GET annotation tells Retrofit that it is a GET request; 'popular' is the end point
    suspend fun getPopularMovies(
        @Query("api_key")
        apiKey: String = Constants.API_KEY
    ): MovieResponse

    @GET("top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key")
        apiKey: String = Constants.API_KEY
    ): MovieResponse

    @GET("{movie_id}") // API call to get detailed movie info
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Long,
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): Movie

    @GET("{movie_id}/reviews")
    suspend fun getMovieReviews(
        @Path("movie_id") movieId: Long,
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): ReviewResponse

    @GET("{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Long,
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): VideoResponse
}