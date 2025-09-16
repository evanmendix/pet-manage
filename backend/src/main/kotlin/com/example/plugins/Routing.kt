package com.example.plugins

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
            // The /api/v1 routes are now defined within their respective feature files
            // and are loaded under the 'authenticate' block.
            authenticate {
                feedingRoutes()
                userRoutes()
            }
        }
    }
}
