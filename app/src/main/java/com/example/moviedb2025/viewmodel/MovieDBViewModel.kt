package com.example.moviedb2025.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.moviedb2025.MovieDBApplication
import com.example.moviedb2025.database.MoviesRepository
import com.example.moviedb2025.models.Movie
import com.example.moviedb2025.models.Review
import com.example.moviedb2025.utils.Constants

import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

// Use sealed interface when loading from database/network
sealed interface MovieListUiState {
    data class Success(val movies: List<Movie>) : MovieListUiState
    object Error : MovieListUiState
    object Loading : MovieListUiState
}

sealed interface SelectedMovieUiState {
    data class Success(val movie: Movie) : SelectedMovieUiState
    object Error : SelectedMovieUiState
    object Loading : SelectedMovieUiState
}

sealed interface ReviewsUiState {
    data class Success(val reviews: List<Review>) : ReviewsUiState
    object Error : ReviewsUiState
    object Loading : ReviewsUiState
}

sealed interface VideosUiState {
    data class Success(val videoKey: String) : VideosUiState
    object Error : VideosUiState
    object Loading : VideosUiState
}


open class MovieDBViewModel(private val moviesRepository: MoviesRepository) : ViewModel() {

    var movieListUiState: MovieListUiState by mutableStateOf(MovieListUiState.Loading)
        private set

    var selectedMovieUiState: SelectedMovieUiState by mutableStateOf(SelectedMovieUiState.Loading)
        private set

    var reviewsUiState: ReviewsUiState by mutableStateOf(ReviewsUiState.Loading)
        private set

    var videosUiState: VideosUiState by mutableStateOf(VideosUiState.Loading)
        private set

    init {
        getPopularMovies()
    }

    private fun getTopRatedMovies() {
        viewModelScope.launch {
            movieListUiState = MovieListUiState.Loading
            movieListUiState = try {
                MovieListUiState.Success(moviesRepository.getTopRatedMovies().results)
            } catch (e: IOException) {
                MovieListUiState.Error
            } catch (e: HttpException) {
                MovieListUiState.Error
            }
        }
    }

    fun getPopularMovies() {
        viewModelScope.launch { //launch coroutine using viewModelScope.launch
            movieListUiState = MovieListUiState.Loading
            movieListUiState = try {
                MovieListUiState.Success(moviesRepository.getPopularMovies().results)
            } catch (e: IOException) {
                MovieListUiState.Error
            } catch (e: HttpException) {
                MovieListUiState.Error
            }
        }
    }

    fun setSelectedMovie(movie: Movie) {
        viewModelScope.launch {
            selectedMovieUiState = SelectedMovieUiState.Loading
            selectedMovieUiState = try {
                // Fetch full movie details from the API
                val fullMovie = moviesRepository.getMovieDetails(movie.id)
                SelectedMovieUiState.Success(fullMovie)
            } catch (e: IOException) {
                SelectedMovieUiState.Error
            } catch (e: HttpException) {
                SelectedMovieUiState.Error
            }
        }
    }


    // NEW: List of favorite movies
    open var favoriteMovies: List<Movie> by mutableStateOf(emptyList()) // No need for sealed interface as only instant local memory update
        private set

    // NEW: Add movie to favorites
    fun addToFavorites(movie: Movie) {
        if (favoriteMovies.none { it.id == movie.id }) {
            favoriteMovies = favoriteMovies + movie
        }
    }

    // NEW: Remove movie from favorites
    fun removeFromFavorites(movie: Movie) {
        favoriteMovies = favoriteMovies.filterNot { it.id == movie.id }
    }

    fun getMovieReviews(movieId: Long) {
        viewModelScope.launch {
            reviewsUiState = ReviewsUiState.Loading
            reviewsUiState = try {
                ReviewsUiState.Success(moviesRepository.getMovieReviews(movieId).results)
            } catch (e: IOException) {
                ReviewsUiState.Error
            } catch (e: HttpException) {
                ReviewsUiState.Error
            }
        }
    }

    fun getMovieVideos(movieId: Long) {
        viewModelScope.launch {
            println("Fetching videos for movieId=$movieId using API key: ${Constants.API_KEY}")
            videosUiState = VideosUiState.Loading
            videosUiState = try {
                val videos = moviesRepository.getMovieVideos(movieId).results

                val acceptedTypes = listOf("Trailer", "Teaser", "Clip", "Featurette")

                val youtubeVideo = videos.firstOrNull {
                    it.site.equals("YouTube", ignoreCase = true) &&
                            acceptedTypes.contains(it.type)
                }

                if (youtubeVideo != null) {
                    VideosUiState.Success(youtubeVideo.key)
                } else {
                    println("No valid YouTube videos found.")
                    VideosUiState.Error
                }
            } catch (e: IOException) {
                println("Failed to fetch video: ${e.message}")
                VideosUiState.Error
            } catch (e: HttpException) {
                println("Failed to fetch video: ${e.message}")
                VideosUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MovieDBApplication)
                val moviesRepository = application.container.moviesRepository
                MovieDBViewModel(moviesRepository = moviesRepository)
            }
        }
    }
}