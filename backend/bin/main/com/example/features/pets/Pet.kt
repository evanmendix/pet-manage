package com.example.features.pets

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Pet(
    val id: String,
    val name: String,
    val photoUrl: String?
)

object Pets : Table("pets") {
    val id = varchar("id", 255)
    val name = varchar("name", 255)
    val photoUrl = varchar("photo_url", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}
