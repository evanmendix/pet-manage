package com.example.catfeeder.data.repository

import com.example.catfeeder.data.model.Feeding
import com.example.catfeeder.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedingRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getFeedingStatus() = apiService.getFeedingStatus()

    suspend fun addFeeding(feeding: Feeding) = apiService.addFeeding(feeding)

    suspend fun getFeedings(): List<Feeding> = apiService.getFeedings()
}
