package com.example.catfeeder.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Feeding(
    val id: String? = null,
    val userId: String,
    val user: User? = null, // Include user info
    val timestamp: Long,
    val type: String, // "meal" or "snack"
    val photoUrl: String? = null
)

@Serializable
data class FeedingStatusResponse(
    val isFed: Boolean,
    val message: String,
    val lastFeeding: Feeding? = null
)
