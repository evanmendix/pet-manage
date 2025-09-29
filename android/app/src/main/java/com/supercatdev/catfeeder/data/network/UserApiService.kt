package com.supercatdev.catfeeder.data.network

import com.supercatdev.catfeeder.data.model.BatchUserRequest
import com.supercatdev.catfeeder.data.model.User
import com.supercatdev.catfeeder.data.network.dto.UpdateUserRequest
import com.supercatdev.catfeeder.data.network.dto.UploadResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface UserApiService {

    @Multipart
    @POST("users/profile")
    suspend fun uploadProfilePicture(
        @Part profilePicture: MultipartBody.Part
    ): UploadResponse

    @POST("users/batch")
    suspend fun getUsers(@Body request: BatchUserRequest): List<User>

    @PUT("users/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: String,
        @Body request: UpdateUserRequest
    ): User
}