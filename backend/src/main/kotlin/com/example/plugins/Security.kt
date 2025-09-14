package com.example.plugins

import com.example.security.firebase
import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureSecurity() {
    install(Authentication) {
        firebase()
    }
}
