package com.example.moviedb2025.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Video(

    @SerialName(value = "name")
    val name: String,

    @SerialName(value = "key")
    val key: String,

    @SerialName(value = "site")
    val site: String,

    @SerialName(value = "type")
    val type: String,

    @SerialName(value = "official")
    val official: Boolean

)

