package com.example.features.user

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val name: String?,
    val profilePictureUrl: String?
)
