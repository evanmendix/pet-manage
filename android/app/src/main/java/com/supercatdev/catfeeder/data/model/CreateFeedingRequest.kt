package com.supercatdev.catfeeder.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateFeedingRequest(
    val petId: String,
    val timestamp: Long,
    val type: String, // "meal" or "snack"
    val photoUrl: String? = null,
    val force: Boolean = false
)