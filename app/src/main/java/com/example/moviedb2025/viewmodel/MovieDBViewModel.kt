package com.example.moviedb2025.viewmodel

import androidx.lifecycle.ViewModel
import com.example.moviedb2025.database.MovieDBUIState
import com.example.moviedb2025.models.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MovieDBViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MovieDBUIState())
    val uiState: StateFlow<MovieDBUIState> = _uiState.asStateFlow()

    fun setSelectedMovie(movie: Movie) {
        _uiState.update { currentState ->
            currentState.copy(selectedMovie = movie)
        }
    }

    fun addToFavorites(movie: Movie) {
        _uiState.update { currentState ->
            if (!currentState.favorites.any { it.id == movie.id }) {
                currentState.copy(favorites = currentState.favorites + movie)
            } else currentState
        }
    }

    fun removeFromFavorites(movie: Movie) {
        _uiState.update { currentState ->
            currentState.copy(favorites = currentState.favorites.filterNot { it.id == movie.id })
        }
    }

    fun isFavorite(movie: Movie): Boolean {
        return _uiState.value.favorites.any { it.id == movie.id }
    }
}