package com.example.catfeeder.data.network

import com.example.catfeeder.data.model.Feeding
import com.example.catfeeder.data.model.FeedingStatusResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("api/v1/status/current")
    suspend fun getFeedingStatus(): FeedingStatusResponse

    @POST("api/v1/feedings")
    suspend fun addFeeding(@Body feeding: Feeding): Feeding
}
