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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.moviedb2025.models.Review
import com.example.moviedb2025.viewmodel.MovieDBViewModel
import com.example.moviedb2025.viewmodel.ReviewsUiState
import com.example.moviedb2025.viewmodel.VideosUiState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

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
        //Android Youtube Player
        when (val state = viewModel.videosUiState) {
            is VideosUiState.Success -> {
                YouTubePlayerComposable(videoId = state.videoKey)
            }
            is VideosUiState.Loading -> Text("Loading trailer...", Modifier.padding(16.dp))
            is VideosUiState.Error -> Text("Trailer not available.", Modifier.padding(16.dp))
        }

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
                    Spacer(modifier = Modifier.height(16.dp))

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
fun YouTubePlayerComposable(videoId: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    AndroidView(
        factory = {
            YouTubePlayerView(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                lifecycle.addObserver(this)

                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(videoId, 0f)
                    }
                })
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    )
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