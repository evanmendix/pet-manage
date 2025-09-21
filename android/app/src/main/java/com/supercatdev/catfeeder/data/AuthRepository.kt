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

suspend fun signInAnonymously(): FirebaseUser {
    // Let the await() call throw an exception on failure, which will be caught by the ViewModel.
    val authResult = firebaseAuth.signInAnonymously().await()
    return authResult.user!!
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
