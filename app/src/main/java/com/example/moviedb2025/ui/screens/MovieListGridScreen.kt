package com.example.moviedb2025.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.moviedb2025.models.Movie
import com.example.moviedb2025.ui.GenreChips
import com.example.moviedb2025.utils.Constants
import com.example.moviedb2025.viewmodel.MovieListUiState

@Composable
fun MovieListGridScreen(
    movieListUiState: MovieListUiState,
    onMovieListItemClicked: (Movie) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 2 // You can change this value to set the number of columns
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
    ) {when(movieListUiState) {
        is MovieListUiState.Success -> {
            items(movieListUiState.movies) { movie ->
                MovieListGridItemCard(
                    movie = movie,
                    onMovieListItemClicked,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        is MovieListUiState.Loading -> {
            item {
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        is MovieListUiState.Error -> {
            item {
                Text(
                    text = "Error: Something went wrong!",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
}

@Composable
fun MovieListGridItemCard(
    movie: Movie,
    onMovieListItemClicked: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RectangleShape,
        onClick = {
            onMovieListItemClicked(movie)
        }
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = Constants.POSTER_IMAGE_BASE_URL + Constants.POSTER_IMAGE_BASE_WIDTH + movie.posterPath,
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 3f)
                )
            }
            Column(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = movie.releaseDate,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun MovieListGridItemCardPreview() {
//    val sampleMovie = Movie(
//        id = 1,
//        title = "A Minecraft Movie",
//        posterPath = "/yFHHfHcUgGAxziP1C3lLt0q2T4s.jpg",
//        backdropPath = "/2Nti3gYAX513wvhp8IiLL6ZDyOm.jpg",
//        releaseDate = "2025-03-31",
//        overview = "Four misfits find themselves struggling with ordinary problems when they are suddenly pulled through a mysterious portal into the Overworld: a bizarre, cubic wonderland that thrives on imagination. To get back home, they'll have to master this world while embarking on a magical quest with an unexpected, expert crafter, Steve.",
//        genresIds = listOf(28),
//        homepage = "https://www.minecraft-movie.com",
//        imdbId = "tt3566834"
//    )
//
//    MaterialTheme {
//        MovieListGridItemCard(
//            movie = sampleMovie,
//            onMovieListItemClicked = {},
//            modifier = Modifier
//                .padding(8.dp)
//                .width(180.dp) // Set fixed width to simulate grid column
//        )
//    }
//}

