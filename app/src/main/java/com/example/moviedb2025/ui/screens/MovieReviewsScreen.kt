package com.example.moviedb2025.ui.screens

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.moviedb2025.models.Review
import com.example.moviedb2025.utils.Constants.EXAMPLE_VIDEO_URI
import com.example.moviedb2025.viewmodel.MovieDBViewModel
import com.example.moviedb2025.viewmodel.ReviewsUiState
import com.example.moviedb2025.viewmodel.VideosUiState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.clickable

@Composable
fun MovieReviewsScreen(
    movieId: Long,
    viewModel: MovieDBViewModel
) {
    // Load reviews and video when entering this screen
    LaunchedEffect(movieId) {
        viewModel.getMovieReviews(movieId)
        viewModel.getMovieVideos(movieId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // ExoPlayerView at the top
        ExoPlayerView()

        //Reviews section
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
}

@Composable
fun ReviewCard(review: Review) {
    var expanded by remember { mutableStateOf(false) }

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
                maxLines = if (expanded) Int.MAX_VALUE else 6,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (expanded) "See less" else "See more...",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { expanded = !expanded }
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun ExoPlayerView() {
    val context = LocalContext.current
    val exoPlayer = ExoPlayer.Builder(context).build()

    val mediaSource = remember(EXAMPLE_VIDEO_URI) {
        MediaItem.fromUri(EXAMPLE_VIDEO_URI)
    }

    if (!LocalInspectionMode.current) {
        LaunchedEffect(mediaSource) {
            exoPlayer.setMediaItem(mediaSource)
            exoPlayer.prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}
