package com.example.moviedb2025.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moviedb2025.models.Review
import com.example.moviedb2025.viewmodel.MovieDBViewModel
import com.example.moviedb2025.viewmodel.ReviewsUiState

@Composable
fun MovieReviewsScreen(
    movieId: Long,
    viewModel: MovieDBViewModel
) {
    // Load reviews when entering this screen
    androidx.compose.runtime.LaunchedEffect(movieId) {
        viewModel.getMovieReviews(movieId)
    }

    when (val state = viewModel.reviewsUiState) {
        is ReviewsUiState.Loading -> {
            Text(text = "Loading reviews...", modifier = Modifier.padding(16.dp))
        }
        is ReviewsUiState.Error -> {
            Text(text = "Failed to load reviews.", modifier = Modifier.padding(16.dp))
        }
        is ReviewsUiState.Success -> {
            if (state.reviews.isEmpty()) {
                Text(text = "No reviews available.", modifier = Modifier.padding(16.dp))
            } else {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.reviews) { review ->
                        ReviewCard(review)
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = review.author,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = review.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 6
            )
        }
    }
}
