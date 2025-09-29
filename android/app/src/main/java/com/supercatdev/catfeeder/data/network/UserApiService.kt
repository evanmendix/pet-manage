package com.supercatdev.catfeeder.data.network

import com.supercatdev.catfeeder.data.model.BatchUserRequest
import com.supercatdev.catfeeder.data.model.User
import com.supercatdev.catfeeder.data.network.dto.UpdateUserRequest
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApiService {
    @POST("users/batch")
    suspend fun getUsers(@Body request: BatchUserRequest): List<User>

    @PUT("users/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: String,
        @Body request: UpdateUserRequest
    ): User
}