package com.example

import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

import com.example.core.FirebaseAdmin

// main function starts the Ktor server
fun main() {
    FirebaseAdmin.init()
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

import com.example.plugins.configureSecurity

// Application module where we configure plugins and routes
fun Application.module() {
    configureSerialization()
    configureSecurity()
    configureRouting()
}
