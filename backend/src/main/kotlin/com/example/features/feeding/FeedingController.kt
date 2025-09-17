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
            val startTime = call.request.queryParameters["startTime"]?.toLongOrNull()
            val endTime = call.request.queryParameters["endTime"]?.toLongOrNull()
            call.respond(feedingService.getFeedings(startTime, endTime))
        }

        post {
            try {
                val force = call.request.queryParameters["force"]?.toBoolean() ?: false
                val feeding = call.receive<Feeding>()
                val newFeeding = feedingService.addFeeding(feeding, force)
                call.respond(HttpStatusCode.Created, newFeeding)
            } catch (e: DuplicateFeedingException) {
                call.respond(HttpStatusCode.Conflict, mapOf("error" to (e.message ?: "Duplicate feeding detected")))
            }
        }

        post("/overwrite") {
            val feeding = call.receive<Feeding>()
            val updatedFeeding = feedingService.overwriteLastMeal(feeding)
            call.respond(HttpStatusCode.OK, updatedFeeding)
        }
    }
}
