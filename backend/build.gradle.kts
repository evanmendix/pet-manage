plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
    application
}

application {
    mainClass.set("com.example.ApplicationKt")
}



dependencies {
    // Ktor Core
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")

    // Content Negotiation for JSON
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")

    // Authentication
    implementation("io.ktor:ktor-server-auth-jvm")

    // Monitoring / Call Logging
    implementation("io.ktor:ktor-server-call-logging-jvm")

    // Firebase Admin SDK
    implementation("com.google.firebase:firebase-admin:9.5.0")

    // Database - Exposed for SQL and PostgreSQL driver
    implementation("org.jetbrains.exposed:exposed-core:0.52.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.52.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.52.0")
    implementation("org.postgresql:postgresql:42.7.3")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.6")

    // Testing
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}
