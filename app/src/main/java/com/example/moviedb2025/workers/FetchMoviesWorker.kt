package com.example.moviedb2025.workers

import android.content.Context
import android.util.Log
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
        const val VIEW_POPULAR    = "popular"
        const val VIEW_TOP_RATED  = "top_rated"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val viewState = inputData.getString(KEY_VIEW_STATE) ?: VIEW_POPULAR
        Log.d("FetchMoviesWorker", "Running worker with viewState = $viewState")

        return@withContext try {
            val resp = when (viewState) {
                VIEW_TOP_RATED -> RetrofitInstance.api.getTopRatedMovies(Constants.API_KEY)
                else -> RetrofitInstance.api.getPopularMovies(Constants.API_KEY)
            }

            Log.d("FetchMoviesWorker", "API call successful. Results: ${resp.results.size}")

            val list = resp.results.map {
                Log.d("FetchMoviesWorker", "Mapping movie: ${it.title}")
                it.toCached(viewState)
            }

            Log.d("FetchMoviesWorker", "Mapped all movies. Count: ${list.size}")

            val db = MovieDatabase.getDatabase(applicationContext)
            db.cachedMovieDao().insertAll(list)

            Log.d("FetchMoviesWorker", "Inserted into DB.")

            Result.success()
        } catch (e: Exception) {
            Log.e("FetchMoviesWorker", "Worker failed: ${e.message}", e)
            Result.retry()
        }
    }
}




