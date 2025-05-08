package com.example.moviedb2025.viewmodel

import android.content.Context
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
import com.example.moviedb2025.database.SavedMoviesRepository
import com.example.moviedb2025.database.WorkManagerRepository
import com.example.moviedb2025.models.Movie
import com.example.moviedb2025.models.Review
import com.example.moviedb2025.utils.Constants
import kotlinx.coroutines.flow.first
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
    data class Success(val movie: Movie, val isFavorite: Boolean) : SelectedMovieUiState
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


class MovieDBViewModel(
    private val moviesRepository: MoviesRepository,
    private val savedMoviesRepository: SavedMoviesRepository,
    private val workManagerRepository: WorkManagerRepository
) : ViewModel() {

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

    private var currentListType: String by mutableStateOf("popular") // Default to popular

    fun getTopRatedMovies() {
        viewModelScope.launch {
            movieListUiState = MovieListUiState.Loading
            currentListType = "top_rated"

            try {
                // Try to get cached data first
                val cachedMovies = moviesRepository.getCachedMovies("top_rated").first()
                if (cachedMovies.isNotEmpty()) {
                    movieListUiState = MovieListUiState.Success(cachedMovies)
                    return@launch
                }

                // If no cache, fetch from network
                workManagerRepository.enqueueFetchMoviesWork("top_rated")
                workManagerRepository.enqueueCleanupWork("top_rated")

                val networkMovies = moviesRepository.getTopRatedMovies().results
                movieListUiState = MovieListUiState.Success(networkMovies)

            } catch (e: IOException) {
                // Handle offline case
                val fallback = moviesRepository.getCachedMovies("top_rated").first()
                movieListUiState = if (fallback.isNotEmpty()) {
                    MovieListUiState.Success(fallback)
                } else {
                    MovieListUiState.Error
                }
            } catch (e: HttpException) {
                movieListUiState = MovieListUiState.Error
            }
        }
    }

//    fun getTopRatedMovies() {
//        viewModelScope.launch {
//            movieListUiState = MovieListUiState.Loading
//            movieListUiState = try {
//                MovieListUiState.Success(moviesRepository.getTopRatedMovies().results)
//            } catch (e: IOException) {
//                MovieListUiState.Error
//            } catch (e: HttpException) {
//                MovieListUiState.Error
//            }
//        }
//    }

    fun getPopularMovies() {
        viewModelScope.launch {
            movieListUiState = MovieListUiState.Loading

            workManagerRepository.enqueueFetchMoviesWork("popular")
            workManagerRepository.enqueueCleanupWork("popular")

            movieListUiState = try {
                MovieListUiState.Success(moviesRepository.getPopularMovies().results)
            } catch (e: IOException) {
                MovieListUiState.Error
            } catch (e: HttpException) {
                MovieListUiState.Error
            }
        }
    }

//    fun getPopularMovies() {
//        viewModelScope.launch { //launch coroutine using viewModelScope.launch
//            movieListUiState = MovieListUiState.Loading
//            movieListUiState = try {
//                MovieListUiState.Success(moviesRepository.getPopularMovies().results)
//            } catch (e: IOException) {
//                MovieListUiState.Error
//            } catch (e: HttpException) {
//                MovieListUiState.Error
//            }
//        }
//    }

    fun setSelectedMovie(movie: Movie) {
        viewModelScope.launch {
            selectedMovieUiState = SelectedMovieUiState.Loading
            try {
                // Try to fetch full movie details from the API
                val fullMovie = moviesRepository.getMovieDetails(movie.id)
                val isFavorite = savedMoviesRepository.getMovie(movie.id) != null
                selectedMovieUiState = SelectedMovieUiState.Success(fullMovie, isFavorite)
            } catch (e: IOException) {
                try {
                    // If API fails (e.g., offline), try to load from Room
                    val fallbackMovie = savedMoviesRepository.getMovie(movie.id)
                    if (fallbackMovie != null) {
                        selectedMovieUiState = SelectedMovieUiState.Success(fallbackMovie, true)
                    } else {
                        selectedMovieUiState = SelectedMovieUiState.Error
                    }
                } catch (e2: Exception) {
                    selectedMovieUiState = SelectedMovieUiState.Error
                }
            } catch (e: HttpException) {
                selectedMovieUiState = SelectedMovieUiState.Error
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

    fun getSavedMovies() {
        viewModelScope.launch {
            movieListUiState = MovieListUiState.Loading
            movieListUiState = try {
                MovieListUiState.Success(savedMoviesRepository.getSavedMovies())
            } catch (e: IOException) {
                MovieListUiState.Error
            } catch (e: HttpException) {
                MovieListUiState.Error
            }
        }
    }

    fun saveMovie(movie: Movie){
        viewModelScope.launch {
            savedMoviesRepository.insertMovie(movie)
            selectedMovieUiState = SelectedMovieUiState.Success(movie, true)
        }
    }

    fun deleteMovie(movie: Movie){
        viewModelScope.launch {
            savedMoviesRepository.deleteMovie( movie)
            selectedMovieUiState = SelectedMovieUiState.Success(movie, true)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MovieDBApplication)
                val moviesRepository = application.container.moviesRepository
                val savedMoviesRepository = application.container.savedMoviesRepository
                val workManagerRepository = WorkManagerRepository(application.applicationContext)  // 👈 ADD THIS

                MovieDBViewModel(
                    moviesRepository = moviesRepository,
                    savedMoviesRepository = savedMoviesRepository,
                    workManagerRepository = workManagerRepository  // 👈 PASS IT IN
                )
            }
        }

    }
}