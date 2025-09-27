package com.supercatdev.catfeeder.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BatchUserRequest(
    val userIds: List<String>
)