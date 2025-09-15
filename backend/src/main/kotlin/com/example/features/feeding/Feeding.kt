package com.example.features.feeding

import com.google.cloud.firestore.annotation.DocumentId
import kotlinx.serialization.Serializable

@Serializable
data class Feeding(
    @DocumentId val id: String = "",
    val userId: String = "",
    val timestamp: Long = 0L,
    val type: String = "meal",
    val photoUrl: String? = null
)

@Serializable
data class FeedingStatusResponse(
    val isFed: Boolean,
    val message: String,
    val lastFeeding: Feeding? = null
)
