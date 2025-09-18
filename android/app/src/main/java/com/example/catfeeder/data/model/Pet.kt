package com.example.catfeeder.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Pet(
    val id: String,
    val name: String,
    val photoUrl: String? = null,
    val managingUserIds: List<String> = emptyList()
)
