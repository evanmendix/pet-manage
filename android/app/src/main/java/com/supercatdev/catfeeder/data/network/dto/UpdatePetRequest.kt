package com.supercatdev.catfeeder.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePetRequest(
    val name: String,
    val photoUrl: String? = null
)
