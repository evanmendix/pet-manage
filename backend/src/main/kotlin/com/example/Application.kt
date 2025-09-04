package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable

// main function starts the Ktor server
fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

// Application module where we configure plugins and routes
fun Application.module() {
    configureSerialization()
    configureRouting()
}

// Plugin for JSON serialization
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}

// Data class for our response
@Serializable
data class FeedingStatus(val status: String, val lastFed: String?, val message: String)

// Routing configuration
fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello, Cat Feeder API!")
        }

        route("/api/v1") {
            get("/status/current") {
                // TODO: Implement actual logic to check Firestore
                val status = FeedingStatus(
                    status = "not_fed",
                    lastFed = null,
                    message = "The cat has not been fed yet for this mealtime."
                )
                call.respond(status)
            }
        }
    }
}
