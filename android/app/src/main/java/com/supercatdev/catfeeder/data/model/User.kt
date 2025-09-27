package com.supercatdev.catfeeder.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val profilePictureUrl: String? = null
)