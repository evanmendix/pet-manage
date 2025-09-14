package com.example.features.feeding

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.feedingRoutes() {
    val feedingService = FeedingService()

    route("/feedings") {
        get {
            call.respond(feedingService.getRecentFeedings())
        }
        post {
            val feeding = call.receive<Feeding>()
            call.respond(feedingService.addFeeding(feeding))
        }
    }
}
