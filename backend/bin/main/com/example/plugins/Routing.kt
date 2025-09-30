package com.example.plugins

import com.example.features.feeding.feedingRoutes
import com.example.features.pet.petRoutes
import com.example.features.user.userRoutes
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello, Cat Feeder API!")
        }

        // Static file serving for uploaded images.
        // Use a fixed storage path that works for both Windows dev and Docker
        // Windows dev: C:\AppData\pet-manage\storage
        // Docker: /storage (mapped to C:\AppData\pet-manage\storage on host)
        val storagePath = if (System.getProperty("os.name").contains("Windows", ignoreCase = true)) {
            "C:\\AppData\\pet-manage\\storage"
        } else {
            "/storage"
        }
        staticFiles("/storage", File(storagePath)) {
            enableAutoHeadResponse()
        }

        route("/api/v1") {
            petRoutes()
            feedingRoutes()

            // Authenticated routes
            authenticate {
                userRoutes()
            }
        }
    }
}
