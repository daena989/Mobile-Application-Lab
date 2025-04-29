package com.example.moviedb2025.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Review(
    @SerialName(value = "author")
    val author: String,

    @SerialName(value = "content")
    val content: String
)