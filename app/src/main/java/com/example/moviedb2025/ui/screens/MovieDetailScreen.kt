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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.moviedb2025.models.Movie
import com.example.moviedb2025.ui.GenreChips
import com.example.moviedb2025.utils.Constans
import com.example.moviedb2025.viewmodel.MovieDBViewModel


@Composable
fun MovieDetailScreen(
    movie: Movie,
    viewModel: MovieDBViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val isFavorite = uiState.favorites.any { it.id == movie.id }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box {
            AsyncImage(
                model = Constans.BACKDROP_IMAGE_BASE_URL + Constans.BACKDROP_IMAGE_BASE_WIDTH + movie.backdropPath,
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = movie.title,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Favorite Button
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

        // Genre chips
        GenreChips(genres = movie.genres, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))


        // Links Section
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            movie.homepage.let { homepage ->
                LinkCard(text = "Open Homepage") {
                    val intent = Intent(Intent.ACTION_VIEW, homepage.toUri())
                    context.startActivity(intent)
                }
            }

            movie.imdbId.let { imdbId ->
                val imdbUrl = "https://www.imdb.com/title/$imdbId"
                LinkCard(text = "Open in IMDB") {
                    val intent = Intent(Intent.ACTION_VIEW, imdbUrl.toUri()).apply {
                        setPackage("com.imdb.mobile") // Open in IMDB app
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

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = movie.overview,
            style = MaterialTheme.typography.bodySmall,
            overflow = TextOverflow.Ellipsis
        )
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