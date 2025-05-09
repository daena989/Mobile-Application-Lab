package com.example.moviedb2025.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue
import com.example.moviedb2025.utils.Constants

object RetrofitInstance {
    val api: MovieDBApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.MOVIE_LIST_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieDBApiService::class.java)
    }
}
