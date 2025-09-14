plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("io.ktor.plugin") version "2.3.12"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "com.example"
version = "0.0.1"
application {
    mainClass.set("com.example.ApplicationKt")
}

repositories {
    mavenCentral()
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

    // Firebase Admin SDK
    implementation("com.google.firebase:firebase-admin:9.5.0")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.6")

    // Testing
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}
