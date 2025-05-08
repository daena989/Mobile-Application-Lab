package com.example.moviedb2025.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue

object RetrofitInstance {
    val api: MovieDBApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/") // Important: ending with /
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieDBApiService::class.java)
    }
}
