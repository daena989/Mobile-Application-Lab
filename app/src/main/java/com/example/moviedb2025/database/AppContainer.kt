package com.example.moviedb2025.database


import android.content.Context
//import android.provider.SyncStateContract
import com.example.moviedb2025.network.MovieDBApiService
import com.example.moviedb2025.utils.Constants
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

interface AppContainer { // Provides the necessary dependencies (MovieRepository)
    val moviesRepository: MoviesRepository
    val savedMoviesRepository: SavedMoviesRepository
    val workManagerRepository: WorkManagerRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    // Room Database (single instance)
    private val movieDatabase: MovieDatabase by lazy {
        MovieDatabase.getDatabase(context)
    }

    // CachedMovieDao access
    private val cachedMovieDao: CachedMovieDAO by lazy {
        movieDatabase.cachedMovieDao()
    }

    // Retrofit setup (unchanged)
    fun getLoggerInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }

    val movieDBJson = Json { ignoreUnknownKeys = true }

    @OptIn(ExperimentalSerializationApi::class)
    private val retrofit: Retrofit = Retrofit.Builder()
        .client(
            okhttp3.OkHttpClient.Builder()
                .addInterceptor(getLoggerInterceptor())
                .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .build()
        )
        .addConverterFactory(movieDBJson.asConverterFactory("application/json".toMediaType()))
        .baseUrl(Constants.MOVIE_LIST_BASE_URL)
        .build()

    private val retrofitService: MovieDBApiService by lazy {
        retrofit.create(MovieDBApiService::class.java)
    }

    // Updated MoviesRepository with caching support
    override val moviesRepository: MoviesRepository by lazy {
        NetworkMoviesRepository(
            apiService = retrofitService,
            cachedMovieDao = cachedMovieDao
        )
    }

    // SavedMoviesRepository (unchanged)
    override val savedMoviesRepository: SavedMoviesRepository by lazy {
        FavoriteMoviesRepository(movieDatabase.movieDao())
    }

    // Add WorkManagerRepository if needed
    override val workManagerRepository: WorkManagerRepository by lazy {
        WorkManagerRepository(context)
    }
}