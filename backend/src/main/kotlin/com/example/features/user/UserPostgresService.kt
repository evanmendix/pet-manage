package com.example.features.user

import com.example.core.DatabaseFactory
import com.example.security.FirebaseUser
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class UserPostgresService {

    suspend fun getOrCreateUser(principal: FirebaseUser): User {
        return DatabaseFactory.dbQuery {
            val userRow = Users.select { Users.id eq principal.uid }.singleOrNull()
            if (userRow != null) {
                toUser(userRow)
            } else {
                val newUser = User(
                    id = principal.uid,
                    name = principal.name ?: "New User", // Default name for anonymous users
                    profilePictureUrl = principal.picture
                )
                Users.insert {
                    it[id] = newUser.id
                    it[name] = newUser.name
                    it[profilePictureUrl] = newUser.profilePictureUrl
                }
                newUser
            }
        }
    }

    private fun toUser(row: ResultRow): User = User(
        id = row[Users.id],
        name = row[Users.name],
        profilePictureUrl = row[Users.profilePictureUrl]
    )
}
