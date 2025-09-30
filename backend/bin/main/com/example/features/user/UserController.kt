package com.example.features.user

import com.example.security.FirebaseUser
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes() {
    val userService = UserService()

    route("/users") {
        post {
            val principal = call.principal<FirebaseUser>()
            if (principal == null) {
                call.respond(HttpStatusCode.Unauthorized, "Missing or invalid credentials")
                return@post
            }

            val uid = principal.uid
            val request = call.receive<CreateUserRequest>()

            try {
                val newUser = userService.createUser(uid, request)
                call.respond(HttpStatusCode.Created, newUser)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "An error occurred: ${e.message}")
            }
        }
        get("/{userId}") {
            val userId = call.parameters["userId"]
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing userId parameter")
                return@get
            }

            val user = userService.getUser(userId)
            if (user != null) {
                call.respond(user)
            } else {
                call.respond(HttpStatusCode.NotFound, "User not found")
            }
        }

        put("/{userId}") {
            val principal = call.principal<FirebaseUser>()
            val userId = call.parameters["userId"]
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing userId parameter")
                return@put
            }
            // A user can only update their own data.
            if (principal?.uid != userId) {
                call.respond(HttpStatusCode.Forbidden, "You can only update your own profile.")
                return@put
            }

            val request = call.receive<UpdateUserRequest>()
            val updatedUser = userService.updateUser(userId, request)

            if (updatedUser != null) {
                call.respond(HttpStatusCode.OK, updatedUser)
            } else {
                call.respond(HttpStatusCode.NotFound, "User not found")
            }
        }

        post("/batch") {
            val request = call.receive<BatchUserRequest>()
            val users = userService.getUsers(request.userIds)
            call.respond(users)
        }

        post("/profile") {
            val principal = call.principal<FirebaseUser>()
            if (principal == null) {
                call.respond(HttpStatusCode.Unauthorized, "User not authenticated.")
                return@post
            }
            val userId = principal.uid

            var fileUrl: String? = null
            var fileItem: PartData.FileItem? = null

            try {
                val multipart = call.receiveMultipart()
                multipart.forEachPart { part ->
                    if (part is PartData.FileItem) {
                        if (part.name == "profile_picture") {
                            fileItem = part
                        } else {
                            part.dispose()
                        }
                    }
                }

                fileItem?.let {
                    fileUrl = userService.uploadProfilePicture(userId, it)
                }

                if (fileUrl != null) {
                    call.respond(HttpStatusCode.OK, mapOf("profilePictureUrl" to fileUrl))
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Profile picture upload failed or no file was provided.")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error during file upload: ${e.message}")
            } finally {
                fileItem?.dispose()
            }
        }
    }
}
