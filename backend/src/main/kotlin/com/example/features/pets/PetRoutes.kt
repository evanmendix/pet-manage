package com.example.features.pets

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.petRoutes() {
    val petService = PetService()

    // Public endpoint returning all pets. Using a distinct path to avoid clashing with
    // authenticated "/api/v1/pets" defined in com.example.features.pet.PetController.
    route("/api/v1/pets/all") {
        get {
            val pets = petService.getAll()
            call.respond(pets)
        }
    }
}
