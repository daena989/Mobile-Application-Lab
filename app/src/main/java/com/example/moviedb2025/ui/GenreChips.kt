package com.example.moviedb2025.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenreChips(genres: List<String>, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        genres.forEach { genre ->
            GenreChip(genre)
        }
    }
}

@Composable
fun GenreChip(genre: String) {
    Box(
        modifier = Modifier
            .padding(vertical = 1.dp)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(50)
            )
    ) {
        Text(
            text = genre,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}


@Preview
@Composable
fun GenreChipsRowPreview() {
    GenreChips(
        genres = listOf("Action", "Adventure", "Drama", "Comedy", "Fantasy", "Horror", "Sci-Fi", "Thriller")
    )
}
