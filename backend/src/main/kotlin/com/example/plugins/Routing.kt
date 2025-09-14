package com.example.plugins

import com.example.features.feeding.FeedingService
import com.example.features.feeding.feedingRoutes
import com.example.features.user.userRoutes
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class FeedingStatus(val status: String, val lastFed: String?, val message: String)

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello, Cat Feeder API!")
        }

        route("/api/v1") {
            get("/status/current") {
                val feedingService = FeedingService()
                val recentFeedings = feedingService.getRecentFeedings()
                if (recentFeedings.isEmpty()) {
                    val status = FeedingStatus(
                        status = "not_fed",
                        lastFed = null,
                        message = "The cat has not been fed yet."
                    )
                    call.respond(status)
                } else {
                    val lastFeeding = recentFeedings.first()
                    // This is a simplified logic. A real implementation would have more complex
                    // rules to determine if the cat has been fed for the current mealtime.
                    val status = FeedingStatus(
                        status = "fed",
                        lastFed = lastFeeding.timestamp.toString(),
                        message = "The cat was last fed at ${lastFeeding.timestamp}"
                    )
                    call.respond(status)
                }
            }

            authenticate {
                feedingRoutes()
                userRoutes()
            }
        }
    }
}
