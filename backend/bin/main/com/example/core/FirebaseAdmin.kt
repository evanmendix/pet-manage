package com.example.core

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

object FirebaseAdmin {

    fun init() {
        // The Admin SDK automatically discovers credentials via the GOOGLE_APPLICATION_CREDENTIALS
        // environment variable. For local development, you can set this variable to the path
        // of your service account key file.
        // For more information, see:
        // https://firebase.google.com/docs/admin/setup#initialize-sdk
        try {
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .build()

            FirebaseApp.initializeApp(options)
            println("Firebase Admin SDK initialized successfully.")
        } catch (e: Exception) {
            println("Could not initialize Firebase Admin SDK. Make sure GOOGLE_APPLICATION_CREDENTIALS is set.")
            e.printStackTrace()
        }
    }
}
