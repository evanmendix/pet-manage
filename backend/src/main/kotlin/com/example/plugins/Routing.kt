package com.example.plugins

import com.example.features.feeding.FeedingService
import com.example.features.feeding.feedingRoutes
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
            // This endpoint provides a quick status check to prevent double feeding.
            get("/status/current") {
                val feedingService = FeedingService()
                val status = feedingService.getCurrentStatus()
                call.respond(status)
            }

            authenticate {
                feedingRoutes()
                userRoutes()
            }
        }
    }
}
