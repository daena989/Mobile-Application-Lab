package com.example.moviedb2025.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.moviedb2025.ui.GenreChips
import com.example.moviedb2025.utils.Constants
import com.example.moviedb2025.viewmodel.MovieDBViewModel
import com.example.moviedb2025.viewmodel.SelectedMovieUiState


@Composable
fun MovieDetailScreen(
    viewModel: MovieDBViewModel,
    selectedMovieUiState: SelectedMovieUiState,
    modifier: Modifier = Modifier
) {
    val favoriteMovies = viewModel.favoriteMovies  // Now getting the favoriteMovies state
    val context = LocalContext.current

    when (selectedMovieUiState) {
        is SelectedMovieUiState.Success -> {
            val movie = selectedMovieUiState.movie
            val isFavorite = favoriteMovies.any { it.id == movie.id }

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    AsyncImage(
                        model = Constants.BACKDROP_IMAGE_BASE_URL + Constants.BACKDROP_IMAGE_BASE_WIDTH + movie.backdropPath,
                        contentDescription = movie.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }

                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (isFavorite) {
                            viewModel.removeFromFavorites(movie)
                        } else {
                            viewModel.addToFavorites(movie)
                        }
                    },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Text(text = if (isFavorite) "★ Favorited" else "☆ Add to Favorites")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Release Date: ${movie.releaseDate}",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(12.dp))

                GenreChips(
                    genreIds = movie.genresIds, // Pass genre IDs directly
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    movie.homepage?.let { homepage ->
                        if (homepage.isNotEmpty()) {
                            LinkCard(text = "Open Homepage") {
                                val intent = Intent(Intent.ACTION_VIEW, homepage.toUri())
                                context.startActivity(intent)
                            }
                        }
                    }

                    movie.imdbId?.let { imdbId ->
                        if (imdbId.isNotEmpty()) {
                            val imdbUrl = "https://www.imdb.com/title/$imdbId"
                            LinkCard(text = "Open in IMDB") {
                                val intent = Intent(Intent.ACTION_VIEW, imdbUrl.toUri()).apply {
                                    setPackage("com.imdb.mobile")
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (e: ActivityNotFoundException) {
                                    val fallbackIntent = Intent(Intent.ACTION_VIEW, imdbUrl.toUri())
                                    context.startActivity(fallbackIntent)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        is SelectedMovieUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        is SelectedMovieUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Error loading movie details",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}



@Composable
fun LinkCard(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}