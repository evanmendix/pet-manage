package com.example.features.pet

import com.example.security.FirebaseUser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.petRoutes() {
    val petService = PetService()

    route("/pets") {
        // Get all pets in the user's family
        get {
            val principal = call.principal<FirebaseUser>()
            if (principal == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }

            val pets = petService.getPetsForUserFamily(principal.uid)
            if (pets != null) {
                call.respond(pets)
            } else {
                call.respond(HttpStatusCode.NotFound, "Could not find family for user.")
            }
        }

        // Create a new pet in the user's family
        post {
            val principal = call.principal<FirebaseUser>()
            if (principal == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }

            val request = call.receive<CreatePetRequest>()
            val newPet = petService.createPet(principal.uid, request.name, request.photoUrl)

            if (newPet != null) {
                call.respond(HttpStatusCode.Created, newPet)
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create pet.")
            }
        }

        route("/{petId}/managers") {
            // Add the authenticated user as a manager for the pet
            post {
                val principal = call.principal<FirebaseUser>()
                val petId = call.parameters["petId"]
                if (principal == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }
                if (petId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing petId")
                    return@post
                }

                val success = petService.addManagerToPet(petId, principal.uid)
                if (success) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Pet not found.")
                }
            }

            // Remove the authenticated user as a manager
            delete {
                val principal = call.principal<FirebaseUser>()
                val petId = call.parameters["petId"]
                if (principal == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@delete
                }
                if (petId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing petId")
                    return@delete
                }

                val success = petService.removeManagerFromPet(petId, principal.uid)
                if (success) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Pet not found.")
                }
            }
        }
    }
}
