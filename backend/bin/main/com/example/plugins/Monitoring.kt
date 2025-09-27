package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.request.*

// A minimal request/response logger that doesn't rely on the CallLogging artifact.
private val SimpleCallLogger = createApplicationPlugin(name = "SimpleCallLogger") {
    onCall { call ->
        val method = call.request.httpMethod.value
        val uri = call.request.uri
        call.application.log.info("Incoming: $method $uri")
    }

    onCallRespond { call, statusCode ->
        val method = call.request.httpMethod.value
        val uri = call.request.uri
        call.application.log.info("Responded: ${call.response.status()?.value ?: "?"} for $method $uri")
    }
}

fun Application.configureMonitoring() {
    install(SimpleCallLogger)
}
