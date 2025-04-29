package com.example.moviedb2025.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewResponse(
    @SerialName(value = "results")
    val results: List<Review>
)