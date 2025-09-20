package com.example.features.pet

import com.example.features.user.Users
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Pets : Table("pets") {
    val id = varchar("id", 255)
    val name = varchar("name", 255)
    val photoUrl = varchar("photo_url", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}

object PetManagers : Table("pet_managers") {
    val petId = varchar("pet_id", 255).references(Pets.id, onDelete = ReferenceOption.CASCADE)
    val userId = varchar("user_id", 255).references(Users.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(petId, userId)
}
