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
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.firstOrNull

// Use sealed interface when loading from database/network
sealed interface MovieListUiState {
    data class Success(val movies: List<Movie>, val isFromCache: Boolean = false) : MovieListUiState
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

enum class ListType {
    POPULAR,
    TOP_RATED,
    SAVED
}

class MovieDBViewModel(
    private val moviesRepository: MoviesRepository,
    private val savedMoviesRepository: SavedMoviesRepository,
    private val workManagerRepository: WorkManagerRepository,
    private val connectivityManager: ConnectivityManager
) : ViewModel() {

    var movieListUiState: MovieListUiState by mutableStateOf(MovieListUiState.Loading)
        private set

    var selectedMovieUiState: SelectedMovieUiState by mutableStateOf(SelectedMovieUiState.Loading)
        private set

    var reviewsUiState: ReviewsUiState by mutableStateOf(ReviewsUiState.Loading)
        private set

    var videosUiState: VideosUiState by mutableStateOf(VideosUiState.Loading)
        private set

    var isNetworkAvailable by mutableStateOf(false)
        private set

    // No need for sealed interface as only instant local memory update
    var favoriteMovies: List<Movie> by mutableStateOf(emptyList())
        private set

    private var cachedMovies: List<Movie>? = null
    private var cachedListType: ListType? = null
    private var currentMovieId: Long? = null
    private var currentListType: ListType = ListType.POPULAR //Default to popular page

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            isNetworkAvailable = true
            println("Network Available: $isNetworkAvailable")
            refreshCurrentList()
            currentMovieId?.let {
                getMovieReviews(it)
                getMovieVideos(it)
            }
        }
        override fun onLost(network: Network) {
            super.onLost(network)
            isNetworkAvailable = false
            println("Network Available: $isNetworkAvailable")
        }
    }

    init {
        registerNetworkCallback()
        getPopularMovies()
    }

    //Lifecycle
    override fun onCleared() {
        super.onCleared()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    //Movie Lists
    fun getTopRatedMovies() {
        viewModelScope.launch {
            movieListUiState = MovieListUiState.Loading
            currentListType = ListType.TOP_RATED

            try {
                val movies = if (isNetworkAvailable) {
                    workManagerRepository.enqueueFetchMoviesWork("top_rated")
                    workManagerRepository.enqueueCleanupWork("top_rated")

                    moviesRepository.getTopRatedMovies().results.also {
                        cachedMovies = it
                        cachedListType = ListType.TOP_RATED
                    }
                } else {
                    if (cachedListType == ListType.TOP_RATED && cachedMovies != null) {
                        cachedMovies!!
                    } else {
                        throw IOException("No cached data available")
                    }
                }

                movieListUiState = MovieListUiState.Success(movies, isFromCache = !isNetworkAvailable)

            } catch (e: IOException) {
                val fallback = moviesRepository.getCachedMovies("top_rated").first()
                movieListUiState = if (fallback.isNotEmpty()) {
                    MovieListUiState.Success(fallback, isFromCache = true)
                } else {
                    MovieListUiState.Error
                }
            } catch (e: HttpException) {
                movieListUiState = MovieListUiState.Error
            }
        }
    }
    fun getPopularMovies() {
        viewModelScope.launch {
            movieListUiState = MovieListUiState.Loading
            currentListType = ListType.POPULAR

            try {
                val movies = if (isNetworkAvailable) {
                    workManagerRepository.enqueueFetchMoviesWork("popular")
                    workManagerRepository.enqueueCleanupWork("popular")

                    moviesRepository.getPopularMovies().results.also {
                        cachedMovies = it
                        cachedListType = ListType.POPULAR
                    }
                } else {
                    if (cachedListType == ListType.POPULAR && cachedMovies != null) {
                        cachedMovies!!
                    } else {
                        throw IOException("No cached data available")
                    }
                }

                movieListUiState = MovieListUiState.Success(movies, isFromCache = !isNetworkAvailable)

            } catch (e: IOException) {
                val fallback = moviesRepository.getCachedMovies("popular").first()
                movieListUiState = if (fallback.isNotEmpty()) {
                    MovieListUiState.Success(fallback, isFromCache = true)
                } else {
                    MovieListUiState.Error
                }
            } catch (e: HttpException) {
                movieListUiState = MovieListUiState.Error
            }
        }
    }
    fun getSavedMovies() {
        currentListType = ListType.SAVED
        cachedMovies = null
        cachedListType = null
        viewModelScope.launch {
            movieListUiState = MovieListUiState.Loading
            movieListUiState = try {
                MovieListUiState.Success(savedMoviesRepository.getSavedMovies(), isFromCache = true)
            } catch (e: IOException) {
                MovieListUiState.Error
            } catch (e: HttpException) {
                MovieListUiState.Error
            }
        }
    }

    //Movie Details
    fun setSelectedMovie(movie: Movie) {
        viewModelScope.launch {
            selectedMovieUiState = SelectedMovieUiState.Loading
            try {
                // First try API
                val fullMovie = moviesRepository.getMovieDetails(movie.id)
                val isFavorite = savedMoviesRepository.getMovie(movie.id) != null
                selectedMovieUiState = SelectedMovieUiState.Success(fullMovie, isFavorite)
            } catch (e: IOException) {
                try {
                    // Try saved favorites from Room
                    val fallbackFavorite = savedMoviesRepository.getMovie(movie.id)
                    if (fallbackFavorite != null) {
                        selectedMovieUiState = SelectedMovieUiState.Success(fallbackFavorite, isFavorite = true)
                        return@launch
                    }

                    // Try cached list
                    val cachedLists = listOf("popular", "top_rated")
                    for (listType in cachedLists) {
                        val cachedMovie = moviesRepository.getCachedMovies(listType).firstOrNull()
                            ?.find { it.id == movie.id }

                        if (cachedMovie != null) {
                            selectedMovieUiState = SelectedMovieUiState.Success(cachedMovie, isFavorite = false)
                            return@launch
                        }
                    }

                    // Nothing worked
                    selectedMovieUiState = SelectedMovieUiState.Error

                } catch (e2: Exception) {
                    selectedMovieUiState = SelectedMovieUiState.Error
                }
            } catch (e: HttpException) {
                selectedMovieUiState = SelectedMovieUiState.Error
            }
        }
    }
    fun getMovieReviews(movieId: Long) {
        viewModelScope.launch {
            currentMovieId = movieId
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
            currentMovieId = movieId
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

    //Storage
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

    //Favourite List
    fun addToFavorites(movie: Movie) {
        if (favoriteMovies.none { it.id == movie.id }) {
            favoriteMovies = favoriteMovies + movie
        }
    }
    fun removeFromFavorites(movie: Movie) {
        favoriteMovies = favoriteMovies.filterNot { it.id == movie.id }
    }

    //Network Tracking
    private fun registerNetworkCallback() {
        try {
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        } catch (e: Exception) {
            isNetworkAvailable = false
        }
    }
    private fun refreshCurrentList() {
        when (currentListType) {
            ListType.POPULAR -> getPopularMovies()
            ListType.TOP_RATED -> getTopRatedMovies()
            ListType.SAVED -> getSavedMovies()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MovieDBApplication)
                val moviesRepository = application.container.moviesRepository
                val savedMoviesRepository = application.container.savedMoviesRepository
                val workManagerRepository = WorkManagerRepository(application.applicationContext)
                val connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                MovieDBViewModel(
                    moviesRepository = moviesRepository,
                    savedMoviesRepository = savedMoviesRepository,
                    workManagerRepository = workManagerRepository,
                    connectivityManager = connectivityManager
                )
            }
        }

    }
}