package com.example.features.feeding

import kotlinx.serialization.Serializable

@Serializable
data class CreateFeedingRequest(
    val petId: String,
    val timestamp: Long,
    val type: String,
    val photoUrl: String? = null,
    val force: Boolean = false
)

@Serializable
data class OverwriteMealRequest(
    val petId: String,
    val timestamp: Long,
    val type: String,
    val photoUrl: String? = null
)
