package com.example.catfeeder.data

import com.example.catfeeder.data.model.Feeding
import com.example.catfeeder.data.network.FeedingApiService
import javax.inject.Inject

class FeedingRepository @Inject constructor(
    private val apiService: FeedingApiService
) {
    suspend fun getFeedings(startTime: Long? = null, endTime: Long? = null): List<Feeding> {
        return apiService.getFeedings(startTime, endTime)
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

    suspend fun addFeeding(feeding: Feeding, force: Boolean = false): Result<Feeding?> {
        return try {
            val response = apiService.addFeeding(feeding, force)
            when {
                response.isSuccessful -> Result.success(response.body())
                response.code() == 409 -> Result.failure(Exception("Duplicate feeding detected."))
                else -> Result.failure(Exception("Failed to add feeding: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun overwriteLastMeal(feeding: Feeding): Result<Feeding?> {
        return try {
            val response = apiService.overwriteLastMeal(feeding)
            if (response.isSuccessful) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Failed to overwrite last meal: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
