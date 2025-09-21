package com.supercatdev.catfeeder.data.network

import com.supercatdev.catfeeder.data.model.Feeding
import retrofit2.Response
import retrofit2.http.*

interface FeedingApiService {

    @GET("feedings")
    suspend fun getFeedings(
        @Query("petId") petId: String,
        @Query("startTime") startTime: Long? = null,
        @Query("endTime") endTime: Long? = null
    ): List<Feeding>

    @GET("status/current")
    suspend fun getCurrentStatus(): Response<Feeding> // Use Response<T> to handle 204 No Content

    @POST("feedings")
    suspend fun addFeeding(
        @Body feeding: Feeding,
        @Query("force") force: Boolean
    ): Response<Feeding> // Use Response<T> to handle 409 Conflict

    @POST("feedings/overwrite")
    suspend fun overwriteLastMeal(@Body feeding: Feeding): Response<Feeding>
}
