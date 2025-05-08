package com.example.moviedb2025.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.moviedb2025.database.MovieDatabase
import com.example.moviedb2025.database.NetworkMoviesRepository
import com.example.moviedb2025.models.CachedMovie
import com.example.moviedb2025.models.Movie
import com.example.moviedb2025.network.MovieDBApiService
import com.example.moviedb2025.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import com.example.moviedb2025.network.RetrofitInstance
import com.example.moviedb2025.models.MovieResponse
import com.example.moviedb2025.models.toMovie
import com.example.moviedb2025.models.toCached

class FetchMoviesWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_VIEW_STATE  = "view_state"
        const val VIEW_POPULAR    = "Popular Movies"
        const val VIEW_TOP_RATED  = "Top Rated Movies"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val viewState = inputData.getString(KEY_VIEW_STATE) ?: VIEW_POPULAR

        return@withContext try {
            // 1) Pick endpoint
            val resp = when(viewState) {
                VIEW_TOP_RATED -> RetrofitInstance.api.getTopRatedMovies(Constants.API_KEY)
                else           -> RetrofitInstance.api.getPopularMovies(Constants.API_KEY)
            }

            // 2) Map & tag
            val list = resp.results
                .map { it.toCached(viewState) }

            // 3) Clear old + insert new
            val db = MovieDatabase.getDatabase(applicationContext)
            db.cachedMovieDao().clearAll()
            db.cachedMovieDao().insertAll(list)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}




