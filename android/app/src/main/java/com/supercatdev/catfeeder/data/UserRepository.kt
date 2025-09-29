package com.supercatdev.catfeeder.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.supercatdev.catfeeder.data.model.BatchUserRequest
import com.supercatdev.catfeeder.data.model.User
import com.supercatdev.catfeeder.data.network.UserApiService
import com.supercatdev.catfeeder.data.network.dto.UpdateUserRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApiService: UserApiService,
    @ApplicationContext private val context: Context
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

    suspend fun updateUser(userId: String, name: String): User {
        val request = UpdateUserRequest(name = name, profilePictureUrl = null)
        val updatedUser = userApiService.updateUser(userId, request)
        userCache[userId] = updatedUser
        return updatedUser
    }

    suspend fun uploadProfilePicture(userId: String, fileUri: Uri): String {
        val contentResolver = context.contentResolver

        // Get file name and type from URI
        val cursor = contentResolver.query(fileUri, null, null, null, null)
        val name = cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                it.getString(nameIndex)
            } else {
                "profile_picture.jpg" // Fallback name
            }
        } ?: "profile_picture.jpg"

        val mimeType = contentResolver.getType(fileUri)

        // Create RequestBody from InputStream
        val inputStream = contentResolver.openInputStream(fileUri)
            ?: throw IllegalStateException("Could not open InputStream for URI: $fileUri")

        val requestBody = inputStream.readBytes().toRequestBody(mimeType?.toMediaTypeOrNull())

        // Create MultipartBody.Part
        val part = MultipartBody.Part.createFormData(
            "profile_picture", // This name must match the name in the backend controller
            name,
            requestBody
        )

        // Call the API
        val response = userApiService.uploadProfilePicture(part)
        val newUrl = response.profilePictureUrl

        // Update the cache
        userCache[userId]?.let {
            userCache[userId] = it.copy(profilePictureUrl = newUrl)
        }

        return newUrl
    }
}