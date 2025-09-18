package com.example.features.pet

import kotlinx.serialization.Serializable

@Serializable
data class CreatePetRequest(
    val name: String,
    val photoUrl: String? = null
)
