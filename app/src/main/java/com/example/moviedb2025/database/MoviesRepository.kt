package com.example.moviedb2025.database

import com.example.moviedb2025.models.Movie
import com.example.moviedb2025.models.MovieResponse
import com.example.moviedb2025.models.ReviewResponse
import com.example.moviedb2025.models.VideoResponse
import com.example.moviedb2025.models.toMovie
import com.example.moviedb2025.network.MovieDBApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface MoviesRepository { // defines the methods for fetching movie data; abstracts data source allowing the rest of the app to interact with the movie data without knowing the details
    suspend fun getPopularMovies(): MovieResponse
    suspend fun getTopRatedMovies(): MovieResponse
    suspend fun getMovieDetails(movieId: Long): Movie
    suspend fun getMovieReviews(movieId: Long): ReviewResponse
    suspend fun getMovieVideos(movieId: Long): VideoResponse
    suspend fun getCachedMovies(type: String): Flow<List<Movie>>
}

// NetworkMR class implements the MovieRepository interface
class NetworkMoviesRepository(
    private val apiService: MovieDBApiService,
    private val cachedMovieDao: CachedMovieDAO
) : MoviesRepository { // Take an instance of MovieDBApiService as a constructor parameter to call diff methods in MovieDBApiService to fetch data and return as MovieResponse
    override suspend fun getPopularMovies(): MovieResponse {
        return apiService.getPopularMovies()
    }

    override suspend fun getTopRatedMovies(): MovieResponse {
        return apiService.getTopRatedMovies()
    }

    override suspend fun getMovieDetails(movieId: Long): Movie {
        return apiService.getMovieDetails(movieId)
    }

    override suspend fun getMovieReviews(movieId: Long): ReviewResponse { // NEW
        return apiService.getMovieReviews(movieId)
    }

    override suspend fun getMovieVideos(movieId: Long): VideoResponse {
        return apiService.getMovieVideos(movieId)
    }

    override suspend fun getCachedMovies(type: String): Flow<List<Movie>> {
        return cachedMovieDao.getCachedMovies(type).map { cachedList ->
            cachedList.map { it.toMovie() }
        }
    }

}

interface SavedMoviesRepository{
    suspend fun getSavedMovies(): List<Movie>
    suspend fun insertMovie(movie: Movie)
    suspend fun getMovie(id: Long): Movie?
    suspend fun deleteMovie(movie: Movie)
}

class FavoriteMoviesRepository(private val movieDAO: MovieDataAcсessObj):SavedMoviesRepository{
    override suspend fun getSavedMovies(): List<Movie> {
        return movieDAO.getSavedMovies()
    }

    override suspend fun insertMovie(movie: Movie) {
        movieDAO.insertMovie(movie)
    }

    override suspend fun getMovie(id: Long): Movie? {
        return movieDAO.getMovie(id)
    }

    override suspend fun deleteMovie(movie: Movie) {
        movieDAO.deleteMovie(movie.id)
    }

}