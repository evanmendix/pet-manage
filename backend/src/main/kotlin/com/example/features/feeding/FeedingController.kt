package com.example.features.feeding

import com.example.features.feeding.DuplicateFeedingException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.publicFeedingRoutes() {
    val feedingService = FeedingService()

    get("/status/current") {
        val currentStatus = feedingService.getCurrentStatus()
        if (currentStatus != null) {
            call.respond(currentStatus)
        } else {
            call.respond(HttpStatusCode.NoContent)
        }
    }
}

fun Route.privateFeedingRoutes() {
    val feedingService = FeedingService()

    route("/feedings") {
        get {
            call.respond(feedingService.getRecentFeedings())
        }

        post {
            try {
                val feeding = call.receive<Feeding>()
                val newFeeding = feedingService.addFeeding(feeding)
                call.respond(HttpStatusCode.Created, newFeeding)
            } catch (e: DuplicateFeedingException) {
                call.respond(HttpStatusCode.Conflict, mapOf("error" to (e.message ?: "Duplicate feeding detected")))
            }
        }
    }
}
