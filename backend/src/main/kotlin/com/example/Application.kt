package com.example

import com.example.core.DatabaseFactory
import com.example.core.FirebaseAdmin
import com.example.plugins.configureRouting
import com.example.plugins.configureMonitoring
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

// main function starts the Ktor server
fun main() {
    FirebaseAdmin.init()
    DatabaseFactory.init()
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

// Application module where we configure plugins and routes
fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureSecurity()
    configureRouting()
}
