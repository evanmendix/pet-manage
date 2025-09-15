package com.example.catfeeder.data.network

import com.example.catfeeder.data.model.Feeding
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FeedingApiService {

    @GET("feedings")
    suspend fun getRecentFeedings(): List<Feeding>

    @GET("status/current")
    suspend fun getCurrentStatus(): Response<Feeding> // Use Response<T> to handle 204 No Content

    @POST("feedings")
    suspend fun addFeeding(@Body feeding: Feeding): Response<Feeding> // Use Response<T> to handle 409 Conflict
}
