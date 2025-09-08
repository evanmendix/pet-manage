package com.example.security

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import io.ktor.server.auth.*

data class FirebaseUser(val uid: String, val name: String?, val email: String?) : Principal

fun AuthenticationConfig.firebase(name: String? = null) {
    val provider = object : AuthenticationProvider(name) {
        override suspend fun onAuthenticate(context: AuthenticationContext) {
            val token = context.call.request.headers["Authorization"]?.removePrefix("Bearer ")
            if (token == null) {
                context.challenge("Firebase", AuthenticationFailedCause.NoCredentials) {
                    it.success()
                }
                return
            }

            try {
                val decodedToken: FirebaseToken = FirebaseAuth.getInstance().verifyIdToken(token)
                val uid = decodedToken.uid
                val name = decodedToken.name
                val email = decodedToken.email
                context.principal(FirebaseUser(uid, name, email))
            } catch (e: Exception) {
                context.challenge("Firebase", AuthenticationFailedCause.Error("Invalid token")) {
                    it.success()
                }
            }
        }
    }
    register(provider)
}
