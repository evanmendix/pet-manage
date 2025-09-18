package com.example.features.pets

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Pet(
    val id: Int,
    val name: String,
    val type: String
)

object Pets : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val type = varchar("type", 50)

    override val primaryKey = PrimaryKey(id)
}
