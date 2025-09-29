package com.supercatdev.catfeeder.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class UploadResponse(
    val profilePictureUrl: String
)