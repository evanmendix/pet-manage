package com.supercatdev.catfeeder.data

import com.supercatdev.catfeeder.data.model.BatchUserRequest
import com.supercatdev.catfeeder.data.model.User
import com.supercatdev.catfeeder.data.network.UserApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApiService: UserApiService
) {
    private val userCache = mutableMapOf<String, User>()

    suspend fun getUsers(userIds: List<String>): Map<String, User> {
        val distinctIds = userIds.distinct()
        val idsToFetch = distinctIds.filter { !userCache.containsKey(it) }

        if (idsToFetch.isNotEmpty()) {
            try {
                val request = BatchUserRequest(userIds = idsToFetch)
                val newUsers = userApiService.getUsers(request)
                newUsers.forEach { user ->
                    userCache[user.id] = user
                }
            } catch (e: Exception) {
                // Log the error or handle it as needed
                // For now, we'll just ignore it and return what's in the cache
            }
        }

        return userCache.filterKeys { it in distinctIds }
    }
}