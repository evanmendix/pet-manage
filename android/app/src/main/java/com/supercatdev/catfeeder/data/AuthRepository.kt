package com.supercatdev.catfeeder.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val firebaseAuth: FirebaseAuth) {

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    suspend fun signInAnonymously(): FirebaseUser? {
        return try {
            val authResult = firebaseAuth.signInAnonymously().await()
            authResult.user
        } catch (e: Exception) {
            // In a real app, handle exceptions more gracefully (e.g., logging)
            e.printStackTrace()
            null
        }
    }

    suspend fun getIdToken(): String? {
        return try {
            getCurrentUser()?.getIdToken(false)?.await()?.token
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
