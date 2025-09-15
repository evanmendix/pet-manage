package com.example.features.user

import com.google.cloud.firestore.annotation.DocumentId
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @DocumentId val id: String = "",
    val name: String = "",
    val profilePictureUrl: String? = null
)
