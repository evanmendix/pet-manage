package com.example.features.pets

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.petRoutes() {
    val petService = PetService()

    route("/api/v1/pets") {
        get {
            val pets = petService.getAll()
            call.respond(pets)
        }
    }
}
