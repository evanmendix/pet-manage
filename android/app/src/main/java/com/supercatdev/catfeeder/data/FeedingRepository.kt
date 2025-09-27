package com.supercatdev.catfeeder.data

import com.supercatdev.catfeeder.data.model.CreateFeedingRequest
import com.supercatdev.catfeeder.data.model.Feeding
import com.supercatdev.catfeeder.data.network.FeedingApiService
import javax.inject.Inject

class FeedingRepository @Inject constructor(
    private val apiService: FeedingApiService
) {
    suspend fun getFeedings(petId: String, startTime: Long? = null, endTime: Long? = null): List<Feeding> {
        return apiService.getFeedings(petId, startTime, endTime)
    }

    suspend fun getCurrentStatus(petId: String): Result<Feeding?> {
        return try {
            val response = apiService.getCurrentStatus(petId)
            if (response.isSuccessful) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Failed to get current status: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addFeeding(request: CreateFeedingRequest): Result<Feeding?> {
        return try {
            val response = apiService.addFeeding(request)
            when {
                response.isSuccessful -> Result.success(response.body())
                response.code() == 409 -> Result.failure(Exception("Duplicate feeding detected."))
                else -> Result.failure(Exception("Failed to add feeding: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun overwriteLastMeal(request: CreateFeedingRequest): Result<Feeding?> {
        return try {
            val response = apiService.overwriteLastMeal(request)
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