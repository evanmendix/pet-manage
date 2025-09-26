package com.supercatdev.catfeeder.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.withTimeout
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
            Log.d("AuthRepository", "Starting anonymous sign-in...")
            val authResult = firebaseAuth.signInAnonymously().await()
            Log.d("AuthRepository", "Anonymous sign-in successful: ${authResult.user?.uid}")
            authResult.user
        } catch (e: Exception) {
            Log.e("AuthRepository", "Anonymous sign-in failed: ${e.message}", e)
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

    suspend fun getOrSignInIdToken(): String? {
        return try {
            Log.d("AuthRepository", "getOrSignInIdToken() called")
            withTimeout(5_000) {
                val currentUser = getCurrentUser()
                Log.d("AuthRepository", "Current user: ${currentUser?.uid ?: "null"}")

                if (currentUser == null) {
                    Log.d("AuthRepository", "No current user, attempting anonymous sign-in...")
                    val signInResult = signInAnonymously()
                    Log.d("AuthRepository", "Sign-in result: ${signInResult?.uid ?: "failed"}")
                }

                Log.d("AuthRepository", "About to call getIdToken()")
                val token = getIdToken()
                Log.d("AuthRepository", "Final token result: ${if (token != null) "success (${token.take(20)}...)" else "null"}")
                token
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error in getOrSignInIdToken: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }
}
