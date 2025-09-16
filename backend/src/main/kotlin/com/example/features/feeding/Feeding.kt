package com.example.features.feeding

import kotlinx.serialization.Serializable

@Serializable
data class Feeding(
    val id: String? = null,
    val userId: String = "",
    val timestamp: Long = 0L,
    val type: String = "", // "meal" or "snack"
    val photoUrl: String? = null
)
