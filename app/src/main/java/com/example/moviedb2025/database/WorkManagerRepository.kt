package com.example.moviedb2025.database

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.moviedb2025.utils.Constants.TAG_OUTPUT
import com.example.moviedb2025.workers.FetchMoviesWorker
import java.util.concurrent.TimeUnit
import com.example.moviedb2025.workers.CleanupWorker


class WorkManagerRepository(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)

    fun enqueueFetchMoviesWork(viewType: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Fetch
        val fetchRequest = OneTimeWorkRequestBuilder<FetchMoviesWorker>()
            .setInputData(workDataOf(FetchMoviesWorker.KEY_VIEW_STATE to viewType))
            .setConstraints(constraints)
            .addTag(TAG_OUTPUT)
            .build()

        // Cleanup
        val cleanupRequest = OneTimeWorkRequestBuilder<CleanupWorker>()
            .setInputData(workDataOf("type" to viewType))
            .build()

        // Chain the workers: Fetch -> Cleanup
        val continuation = workManager.beginUniqueWork( // beginUniqueWork; only want one chain of work to run at a time
            "refresh_$viewType",
            ExistingWorkPolicy.KEEP,
            fetchRequest
        ).then(cleanupRequest)

        // Enqueue the chain
        continuation.enqueue()
    }

    fun enqueueCleanupWork(selectedType: String) {
        val cleanupRequest = OneTimeWorkRequestBuilder<CleanupWorker>()
            .setInputData(workDataOf("type" to selectedType))
            .build()

        workManager.enqueueUniqueWork(
            "cleanup_movies_cache",
            ExistingWorkPolicy.REPLACE,
            cleanupRequest
        )
    }

    fun cancelWork(viewType: String) {
        workManager.cancelUniqueWork("refresh_$viewType")
    }
}
