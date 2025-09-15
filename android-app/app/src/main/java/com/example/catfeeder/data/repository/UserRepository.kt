package com.example.catfeeder.data.repository

import com.example.catfeeder.data.model.User
import com.example.catfeeder.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getUser(userId: String): User? {
        return try {
            apiService.getUser(userId)
        } catch (e: Exception) {
            // Assuming 404 means user not found
            null
        }
    }

    suspend fun createUser(user: User): User {
        return apiService.createUser(user)
    }
}
