package com.example.features.user

import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val id = varchar("id", 255)
    val name = varchar("name", 255)
    val profilePictureUrl = varchar("profile_picture_url", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}
