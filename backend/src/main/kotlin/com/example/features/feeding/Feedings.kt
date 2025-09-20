package com.example.features.feeding

import com.example.features.pet.Pets
import com.example.features.user.Users
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Feedings : Table("feedings") {
    val id = varchar("id", 255)
    val userId = varchar("user_id", 255).references(Users.id, onDelete = ReferenceOption.CASCADE)
    val petId = varchar("pet_id", 255).references(Pets.id, onDelete = ReferenceOption.CASCADE)
    val timestamp = long("timestamp")
    val type = varchar("type", 50) // "meal" or "snack"
    val photoUrl = varchar("photo_url", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}
