package com.example.moviedb2025.ui.screens

//import android.media.browse.MediaBrowser.MediaItem
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.moviedb2025.models.Review
import com.example.moviedb2025.viewmodel.MovieDBViewModel
import com.example.moviedb2025.viewmodel.ReviewsUiState

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.ui.PlayerView
import com.example.moviedb2025.utils.Constants.EXAMPLE_VIDEO_URI
import com.example.moviedb2025.viewmodel.VideosUiState


import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import androidx.compose.ui.platform.LocalLifecycleOwner

@Composable
fun MovieReviewsScreen(
    movieId: Long,
    viewModel: MovieDBViewModel
) {
    // Load reviews when entering this screen
    androidx.compose.runtime.LaunchedEffect(movieId) {
        viewModel.getMovieReviews(movieId)
        viewModel.getMovieVideos(movieId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Exoplayer
        ExoPlayerView()

        //Android Youtube Player
        when (val state = viewModel.videosUiState) {
            is VideosUiState.Success -> {
                YouTubePlayerComposable(videoId = state.videoKey)
            }
            is VideosUiState.Loading -> Text("Loading trailer...", Modifier.padding(16.dp))
            is VideosUiState.Error -> Text("Trailer not available.", Modifier.padding(16.dp))
        }

        // Reviews section
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

@OptIn(UnstableApi::class)
@Composable
fun ExoPlayerView() {
    // Get the current context
    val context = LocalContext.current

    // Initialize ExoPlayer
    val exoPlayer = ExoPlayer.Builder(context).build()

    // Create a MediaSource
    val mediaSource = remember(EXAMPLE_VIDEO_URI) {
        MediaItem.fromUri(EXAMPLE_VIDEO_URI)
    }

    // Set MediaSource to ExoPlayer
    if (!LocalInspectionMode.current) {
        LaunchedEffect(mediaSource) {
            exoPlayer.setMediaItem(mediaSource)
            exoPlayer.prepare()
        }
    }


    // Manage lifecycle events
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Use AndroidView to embed an Android View (PlayerView) into Compose
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp) // Set your desired height
    )
}

//@Preview(showBackground = true)
//@Composable
//fun ExoPlayerPreview() {
//        ExoPlayerView()
//}

