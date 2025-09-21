package com.example.security

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import io.ktor.server.auth.*

data class FirebaseUser(val uid: String, val name: String?, val email: String?, val picture: String?) : Principal

class FirebaseAuthenticationProvider(config: Config) : AuthenticationProvider(config) {
    private val principle: suspend (FirebaseToken) -> Principal? = config.principal

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        val token = context.call.request.headers["Authorization"]?.removePrefix("Bearer ")
        if (token == null) {
            context.challenge("Firebase", AuthenticationFailedCause.NoCredentials) { _, _ -> }
            return
        }

        try {
            val decodedToken: FirebaseToken = FirebaseAuth.getInstance().verifyIdToken(token)
            val principal = principle(decodedToken)
            if (principal != null) {
                context.principal(principal)
            } else {
                context.challenge("Firebase", AuthenticationFailedCause.Error("Invalid principal")) { _, _ -> }
            }
        } catch (e: Exception) {
            context.challenge("Firebase", AuthenticationFailedCause.Error("Invalid token: ${e.message}")) { _, _ -> }
        }
    }

    class Config(name: String?) : AuthenticationProvider.Config(name) {
        var principal: suspend (FirebaseToken) -> Principal? = { token ->
            FirebaseUser(token.uid, token.name, token.email, token.picture)
        }
    }
}

fun AuthenticationConfig.firebase(name: String? = null, configure: FirebaseAuthenticationProvider.Config.() -> Unit = {}) {
    val provider = FirebaseAuthenticationProvider(FirebaseAuthenticationProvider.Config(name).apply(configure))
    register(provider)
}
