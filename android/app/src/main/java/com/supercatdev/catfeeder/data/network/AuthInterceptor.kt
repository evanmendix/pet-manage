package com.supercatdev.catfeeder.data.network

import com.supercatdev.catfeeder.data.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val authRepository: AuthRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = runBlocking {
            // Wait up to 10 seconds for user to be authenticated.
            withTimeoutOrNull(10_000) {
                while (authRepository.getCurrentUser() == null) {
                    delay(100)
                }
                authRepository.getIdToken()
            }
        }

        val requestBuilder = originalRequest.newBuilder()
        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
