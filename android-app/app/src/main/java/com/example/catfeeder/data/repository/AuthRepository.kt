package com.example.catfeeder.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val firebaseAuth: FirebaseAuth) {

    suspend fun signInAnonymously(): String {
        val authResult = firebaseAuth.signInAnonymously().await()
        return authResult.user?.uid ?: throw IllegalStateException("Firebase UID not found")
    }

    fun getCurrentUserUid(): String? {
        return firebaseAuth.currentUser?.uid
    }
}
