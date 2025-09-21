package com.example.features.user

import com.example.core.DatabaseFactory
import com.example.security.FirebaseUser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

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

    suspend fun createUser(uid: String, request: CreateUserRequest): User {
        val newUser = User(
            id = uid,
            name = request.name,
            profilePictureUrl = request.profilePictureUrl
        )
        return DatabaseFactory.dbQuery {
            Users.insert {
                it[id] = newUser.id
                it[name] = newUser.name
                it[profilePictureUrl] = newUser.profilePictureUrl
            }
            newUser
        }
    }

    suspend fun getUser(userId: String): User? {
        return DatabaseFactory.dbQuery {
            Users.select { Users.id eq userId }
                .map { toUser(it) }
                .singleOrNull()
        }
    }

    suspend fun updateUser(userId: String, request: UpdateUserRequest): User? {
        return DatabaseFactory.dbQuery {
            val updatedRows = Users.update({ Users.id eq userId }) {
                request.name?.let { newName -> it[name] = newName }
                request.profilePictureUrl?.let { newUrl -> it[profilePictureUrl] = newUrl }
            }
            if (updatedRows > 0) {
                Users.select { Users.id eq userId }
                    .map { toUser(it) }
                    .singleOrNull()
            } else {
                null
            }
        }
    }

    private fun toUser(row: ResultRow): User = User(
        id = row[Users.id],
        name = row[Users.name],
        profilePictureUrl = row[Users.profilePictureUrl]
    )
}
