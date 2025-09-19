package com.example.features.feeding

import com.example.security.FirebaseUser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.feedingRoutes() {
    val feedingService = FeedingService()

    route("/feedings") {
        authenticate {
            get {
                val principal = call.principal<FirebaseUser>()!!
                val petId = call.request.queryParameters["petId"]
                if (petId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing petId query parameter")
                    return@get
                }

                val startTime = call.request.queryParameters["startTime"]?.toLongOrNull()
                val endTime = call.request.queryParameters["endTime"]?.toLongOrNull()

                val feedings = feedingService.getFeedings(petId, startTime, endTime)
                call.respond(feedings)
            }

            post {
                val principal = call.principal<FirebaseUser>()!!
                val request = call.receive<CreateFeedingRequest>()

                try {
                    val newFeeding = feedingService.addFeeding(principal.uid, request)
                    call.respond(HttpStatusCode.Created, newFeeding)
                } catch (e: DuplicateFeedingException) {
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to (e.message ?: "Duplicate feeding detected")))
                }
            }

            post("/overwrite") {
                val principal = call.principal<FirebaseUser>()!!
                val request = call.receive<OverwriteMealRequest>()
                val updatedFeeding = feedingService.overwriteLastMeal(principal.uid, request)
                call.respond(HttpStatusCode.OK, updatedFeeding)
            }
        }

        // Public route for current status
        get("/status/current") {
            val petId = call.request.queryParameters["petId"]
            if (petId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing petId query parameter")
                return@get
            }
            val currentStatus = feedingService.getCurrentStatus(petId)
            if (currentStatus != null) {
                call.respond(currentStatus)
            } else {
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
