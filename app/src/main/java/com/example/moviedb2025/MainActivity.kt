package com.example.moviedb2025

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.moviedb2025.ui.screens.MovieDBApp
import com.example.moviedb2025.ui.theme.MovieDB2025Theme
import com.example.moviedb2025.viewmodel.MovieDBViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MovieDBViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this, MovieDBViewModel.Factory).get(MovieDBViewModel::class.java)

        // Initialize WorkManagerRepository
        viewModel.setWorkManagerRepo(applicationContext)

        setContent {
            MovieDB2025Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MovieDBApp()
                }
            }
        }

        // Now that the UI is set up, you can safely call your ViewModel methods
        viewModel.getPopularMovies()
    }
}


