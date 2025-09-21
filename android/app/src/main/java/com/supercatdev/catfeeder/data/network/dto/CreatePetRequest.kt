package com.supercatdev.catfeeder.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreatePetRequest(
    val name: String,
    val photoUrl: String? = null
)
