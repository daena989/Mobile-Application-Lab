package com.example.moviedb2025.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.moviedb2025.models.CachedMovie
import com.example.moviedb2025.models.Movie

@Database(entities = [Movie::class, CachedMovie::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class) // Room doesn't directly support List<Int>, you'll need a Type Converter to handle it.
abstract class MovieDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDataAcсessObj
    abstract fun cachedMovieDao(): CachedMovieDAO

    companion object {
        @Volatile
        private var Instance: MovieDatabase? = null

        fun getDatabase(context: Context): MovieDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, MovieDatabase::class.java, "movie_database")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}