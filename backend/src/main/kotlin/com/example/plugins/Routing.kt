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
        // This makes files under the physical path (defined by IMAGE_STORAGE_PATH)
        // accessible via the "/storage" URL path.
        staticFiles("/storage", File(System.getenv("IMAGE_STORAGE_PATH") ?: "/storage")) {
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
