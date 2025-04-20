package com.example.moviedb2025.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moviedb2025.models.Movie

@Composable
fun FavoritesScreen(
    favorites: List<Movie>,
    onMovieClicked: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(favorites) { movie ->
            MovieListItemCard(
                movie = movie,
                onMovieListItemClicked = onMovieClicked,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}