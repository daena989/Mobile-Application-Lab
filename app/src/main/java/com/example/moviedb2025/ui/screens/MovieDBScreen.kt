@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.moviedb2025.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moviedb2025.R
import com.example.moviedb2025.viewmodel.MovieDBViewModel
import androidx.navigation.navArgument
import androidx.navigation.NavType


enum class MovieDBScreen(@StringRes val title: Int){
    List(title = R.string.app_name),
    Detail(title = R.string.movie_detail),
    Favorites(title = R.string.favorites),
    Reviews(title = R.string.reviews)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDBAppBar(
    currentScreen: MovieDBScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    movieDBViewModel: MovieDBViewModel,
    onFavoritesClick: () -> Unit = {} // Add onFavoritesClick for List screen
) {
    var menuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        actions = {
            if (currentScreen == MovieDBScreen.List) {
                var menuExpanded by remember { mutableStateOf(false) }

                IconButton(onClick = {
                    menuExpanded = !menuExpanded
                }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Open menu to select different movie lists"
                    )
                }

                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(
                        onClick = {
                            movieDBViewModel.getPopularMovies()
                            menuExpanded = false
                        },
                        text = { Text(stringResource(R.string.popular_movies)) }
                    )
                    DropdownMenuItem(
                        onClick = {
                            movieDBViewModel.getTopRatedMovies()
                            menuExpanded = false
                        },
                        text = { Text(stringResource(R.string.top_rated_movies)) }
                    )
                    DropdownMenuItem(
                        onClick = {
                            movieDBViewModel.getSavedMovies()
                            menuExpanded = false
                        },
                        text = { Text(stringResource(R.string.saved)) }
                    )
                }
            }
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
//        actions = {
//            if (currentScreen == MovieDBScreen.List) {
//                IconButton(onClick = onFavoritesClick) {
//                    Icon(
//                        imageVector = Icons.Default.Favorite,
//                        contentDescription = stringResource(R.string.favorites)
//                    )
//                }
//            }
//        }
    )
}

@Composable
fun MovieDBApp(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentScreen = when (backStackEntry?.destination?.route?.substringBefore("/")) {
        MovieDBScreen.List.name -> MovieDBScreen.List
        MovieDBScreen.Detail.name -> MovieDBScreen.Detail
        MovieDBScreen.Favorites.name -> MovieDBScreen.Favorites
        MovieDBScreen.Reviews.name -> MovieDBScreen.Reviews // Treat Reviews like Detail for AppBar (or create special logic)
        else -> MovieDBScreen.List
    }

    val movieDBViewModel: MovieDBViewModel = viewModel(factory = MovieDBViewModel.Factory)

    Scaffold(
        topBar = {
            MovieDBAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                movieDBViewModel = movieDBViewModel,
                onFavoritesClick = {
                    navController.navigate(MovieDBScreen.Favorites.name)
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MovieDBScreen.List.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(route = MovieDBScreen.List.name) {
                MovieListGridScreen(
                    movieListUiState = movieDBViewModel.movieListUiState,
                    onMovieListItemClicked = { movie ->
                        movieDBViewModel.setSelectedMovie(movie)
                        navController.navigate(MovieDBScreen.Detail.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
            composable(route = MovieDBScreen.Detail.name) {
                MovieDetailScreen(
                    movieDBViewModel = movieDBViewModel,
                    //selectedMovieUiState = movieDBViewModel.selectedMovieUiState,
                    modifier = Modifier,
                    navController = navController
                )
            }
            composable(route = MovieDBScreen.Favorites.name) {
                FavoritesScreen(
                    favorites = movieDBViewModel.favoriteMovies,
                    onMovieClicked = { movie ->
                        movieDBViewModel.setSelectedMovie(movie)
                        navController.navigate(MovieDBScreen.Detail.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }

            composable(
                route = "${MovieDBScreen.Reviews.name}/{movieId}", //dynamic route; needs movieID to load specific data
                arguments = listOf(navArgument("movieId") { type = NavType.LongType })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getLong("movieId") ?: 0L
                MovieReviewsScreen(
                    movieId = movieId,
                    viewModel = movieDBViewModel
                )
            }
        }
    }
}

