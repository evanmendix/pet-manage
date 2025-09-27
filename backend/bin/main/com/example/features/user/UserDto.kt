package com.example.features.user

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val name: String,
    val profilePictureUrl: String? = null
)

@Serializable
data class BatchUserRequest(
    val userIds: List<String>
)
