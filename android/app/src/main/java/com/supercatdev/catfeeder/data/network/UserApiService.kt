package com.supercatdev.catfeeder.data.network

import com.supercatdev.catfeeder.data.model.BatchUserRequest
import com.supercatdev.catfeeder.data.model.User
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {
    @POST("users/batch")
    suspend fun getUsers(@Body request: BatchUserRequest): List<User>
}