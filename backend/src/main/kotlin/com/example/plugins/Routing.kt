package com.example.plugins

import com.example.features.feeding.privateFeedingRoutes
import com.example.features.feeding.publicFeedingRoutes
import com.example.features.user.userRoutes
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello, Cat Feeder API!")
        }

        route("/api/v1") {
            // Public routes that do not require authentication
            publicFeedingRoutes()

            // Private routes that require authentication
            authenticate {
                privateFeedingRoutes()
                userRoutes()
            }
        }
    }
}
