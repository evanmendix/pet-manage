package com.example.catfeeder.data

import com.example.catfeeder.data.model.Feeding
import com.example.catfeeder.data.network.FeedingApiService
import javax.inject.Inject

class FeedingRepository @Inject constructor(
    private val apiService: FeedingApiService
) {
    suspend fun getRecentFeedings(): List<Feeding> {
        return apiService.getRecentFeedings()
    }

    suspend fun getCurrentStatus(): Result<Feeding?> {
        return try {
            val response = apiService.getCurrentStatus()
            if (response.isSuccessful) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Failed to get current status: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addFeeding(feeding: Feeding): Result<Feeding?> {
        return try {
            val response = apiService.addFeeding(feeding)
            when {
                response.isSuccessful -> Result.success(response.body())
                response.code() == 409 -> Result.failure(Exception("Duplicate feeding detected."))
                else -> Result.failure(Exception("Failed to add feeding: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
