package com.example.features.pet

import com.example.features.user.UserPostgresService
import com.example.security.FirebaseUser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Route.petRoutes() {
    val petService = PetService()
    val userService = UserPostgresService()

    route("/pets") {
        // Get all pets managed by the user
        get {
            val principal = call.principal<FirebaseUser>()!!
            userService.getOrCreateUser(principal)
            val pets = petService.getPetsForUser(principal.uid)
            call.respond(pets)
        }

        // Create a new pet and assign the user as a manager
        post {
            val principal = call.principal<FirebaseUser>()!!
            userService.getOrCreateUser(principal)
            val request = call.receive<CreatePetRequest>()
            if (request == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                return@post
            }
            val newPet = petService.createPet(principal.uid, request)
            call.respond(HttpStatusCode.Created, newPet)
        }

        // Update a pet (name/photo). Any manager of the pet can update.
        put("/{petId}") {
            val principal = call.principal<FirebaseUser>()!!
            userService.getOrCreateUser(principal)
            val petId = call.parameters["petId"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing petId")
            val request = call.receive<UpdatePetRequest>()
            val updated = petService.updatePetIfManager(petId, principal.uid, request)
            if (updated != null) {
                call.respond(HttpStatusCode.OK, updated)
            } else {
                call.respond(HttpStatusCode.NotFound, "Pet not found or user not a manager")
            }
        }

        // Delete a pet (only if requester is a manager). This will cascade delete relationships.
        delete("/{petId}") {
            val principal = call.principal<FirebaseUser>()!!
            userService.getOrCreateUser(principal)
            val petId = call.parameters["petId"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing petId")
            val deleted = petService.deletePetIfManager(petId, principal.uid)
            if (deleted) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound, "Pet not found or user not a manager")
            }
        }

        route("/{petId}/managers") {
            // Add the authenticated user as a manager for the pet
            post {
                val principal = call.principal<FirebaseUser>()!!
                userService.getOrCreateUser(principal)
                val petId = call.parameters["petId"]
                if (petId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing petId")
                    return@post
                }

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
                userService.getOrCreateUser(principal)
                val petId = call.parameters["petId"]
                if (petId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing petId")
                    return@delete
                }

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

@Serializable
data class UpdatePetRequest(
    val name: String,
    val photoUrl: String? = null
)
