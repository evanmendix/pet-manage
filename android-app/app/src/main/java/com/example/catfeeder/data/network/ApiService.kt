package com.example.catfeeder.data.network

import com.example.catfeeder.data.model.Feeding
import com.example.catfeeder.data.model.FeedingStatusResponse
import com.example.catfeeder.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("api/v1/status/current")
    suspend fun getFeedingStatus(): FeedingStatusResponse

    @POST("api/v1/feedings")
    suspend fun addFeeding(@Body feeding: Feeding): Feeding

    @GET("api/v1/users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): User

    @POST("api/v1/users")
    suspend fun createUser(@Body user: User): User

    @GET("api/v1/feedings")
    suspend fun getFeedings(): List<Feeding>
}
