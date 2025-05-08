package com.example.moviedb2025.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.moviedb2025.database.MovieDatabase

class CleanupWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val typeToKeep = inputData.getString("type") ?: return Result.failure()
        val database = MovieDatabase.getDatabase(applicationContext)
        val cachedMovieDao = database.cachedMovieDao()

        return try {
            cachedMovieDao.deleteMoviesNotOfType(typeToKeep)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}