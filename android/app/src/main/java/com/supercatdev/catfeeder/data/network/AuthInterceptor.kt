package com.supercatdev.catfeeder.data.network

import android.util.Log
import com.supercatdev.catfeeder.data.AuthRepository
import kotlinx.coroutines.runBlocking
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
        
        // 添加日誌：請求開始
        Log.d("AuthInterceptor", "Intercepting request: ${originalRequest.method} ${originalRequest.url}")
        
        val token = runBlocking {
            // 添加日誌：開始獲取 token
            Log.d("AuthInterceptor", "Starting to get token...")
            val result = authRepository.getOrSignInIdToken()
            // 添加日誌：token 獲取結果
            if (result != null) {
                Log.d("AuthInterceptor", "Token obtained successfully: ${result.take(20)}...")
            } else {
                Log.w("AuthInterceptor", "Failed to obtain token")
            }
            result
        }

        val requestBuilder = originalRequest.newBuilder()
        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
            Log.d("AuthInterceptor", "Added Authorization header")
        } else {
            Log.w("AuthInterceptor", "No token available, proceeding without Authorization header")
        }

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
