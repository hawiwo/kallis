package de.kallis.model

import kotlinx.serialization.Serializable

@Serializable
data class Article(
    val artikel: String,
    val preis: Double,
    val bestand: Int
)

