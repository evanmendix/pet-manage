package com.supercatdev.catfeeder.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val name: String
)