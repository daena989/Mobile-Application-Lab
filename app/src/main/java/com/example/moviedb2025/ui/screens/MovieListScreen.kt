package com.example.moviedb2025.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.moviedb2025.models.Movie
import com.example.moviedb2025.ui.GenreChips
import com.example.moviedb2025.utils.Constans

@Composable
fun MovieListScreen(
    movieList: List<Movie>,
    onMovieListItemClicked: (Movie) -> Unit,
    modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        LazyColumn {
            items(movieList) { movie ->
                MovieListItemCard(
                    movie = movie,
                    onMovieListItemClicked = onMovieListItemClicked,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}


@Composable
fun MovieListItemCard(movie: Movie,
                      onMovieListItemClicked: (Movie) -> Unit,
                      modifier: Modifier = Modifier) {
    Card(modifier = modifier,
        onClick = {
            onMovieListItemClicked(movie)
        } )
    {
        Row {
            Box {
                AsyncImage(
                    model = Constans.POSTER_IMAGE_BASE_URL + Constans.POSTER_IMAGE_BASE_WIDTH + movie.posterPath,
                    contentDescription = movie.title,
                    modifier = modifier
                        .width(92.dp)
                        .height(138.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = movie.releaseDate,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.size(8.dp))

                GenreChips(genres = movie.genres)

                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.size(8.dp))
            }
        }
    }
}