package com.example.plugins

import com.example.features.feeding.feedingRoutes
import com.example.features.pet.authenticatedPetRoutes
import com.example.features.pet.publicPetRoutes
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
            // Public routes
            publicPetRoutes()
            feedingRoutes()

            // Authenticated routes
            authenticate {
                userRoutes()
                authenticatedPetRoutes()
            }
        }
    }
}
