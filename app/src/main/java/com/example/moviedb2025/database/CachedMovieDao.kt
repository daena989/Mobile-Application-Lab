package com.example.moviedb2025.database

import androidx.room.*
import com.example.moviedb2025.models.CachedMovie
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedMovieDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<CachedMovie>)

    @Query("DELETE FROM cached_movies")
    suspend fun clearAll()

    @Query("DELETE FROM cached_movies WHERE viewType != :type")
    suspend fun deleteMoviesNotOfType(type: String)

    @Query("SELECT * FROM cached_movies WHERE viewType = :viewType")
    fun getCachedMovies(viewType: String): Flow<List<CachedMovie>>
}
