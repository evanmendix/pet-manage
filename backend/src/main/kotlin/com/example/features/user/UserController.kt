package com.example.features.user

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes() {
    val userService = UserService()

    route("/users") {
        post {
            val user = call.receive<User>()
            call.respond(userService.createUser(user))
        }
        put("/{userId}") {
            val userId = call.parameters["userId"]!!
            val user = call.receive<User>()
            val updatedUser = userService.updateUser(userId, user)
            if (updatedUser != null) {
                call.respond(updatedUser)
            } else {
                call.respondText("User not found", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }
    }
}
