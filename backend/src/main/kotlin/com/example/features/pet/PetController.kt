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
        // --- PUBLIC ROUTES ---
        // Get all pets
        get {
            val pets = petService.getAllPets()
            call.respond(pets)
        }

        // --- AUTHENTICATED ROUTES ---
        authenticate {
            // Create a new pet and assign the user as a manager
            post {
                val principal = call.principal<FirebaseUser>()!!
                val request = call.receive<CreatePetRequest>()
                val newPet = petService.createPet(principal.uid, request)
                call.respond(HttpStatusCode.Created, newPet)
            }

            // Delete a pet
            delete("/{petId}") {
                val petId = call.parameters["petId"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing petId")
                val deleted = petService.deletePet(petId)
                if (deleted) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            route("/{petId}/managers") {
                // Add the authenticated user as a manager for the pet
                post {
                    val principal = call.principal<FirebaseUser>()!!
                    val petId = call.parameters["petId"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing petId")

                    val success = petService.addManagerToPet(petId, principal.uid)
                    if (success) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Pet not found or manager already exists.")
                    }
                }

                // Remove the authenticated user as a manager
                delete {
                    val principal = call.principal<FirebaseUser>()!!
                    val petId = call.parameters["petId"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing petId")

                    val success = petService.removeManagerFromPet(petId, principal.uid)
                    if (success) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Pet or manager not found.")
                    }
                }
            }
        }
    }
}
