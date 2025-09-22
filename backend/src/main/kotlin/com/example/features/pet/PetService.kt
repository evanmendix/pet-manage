package com.example.features.pet

import com.example.core.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class PetService {

    suspend fun createPet(userId: String, request: CreatePetRequest): Pet {
        val petId = UUID.randomUUID().toString()
        val newPet = Pet(
            id = petId,
            name = request.name,
            photoUrl = request.photoUrl,
            managingUserIds = listOf(userId)
        )

        dbQuery {
            Pets.insert {
                it[id] = newPet.id
                it[name] = newPet.name
                it[photoUrl] = newPet.photoUrl
            }
            PetManagers.insert {
                it[PetManagers.petId] = newPet.id
                it[PetManagers.userId] = userId
            }
        }
        return newPet
    }

    suspend fun getPetsForUser(userId: String): List<Pet> {
        return dbQuery {
            val petIds = PetManagers
                .select { PetManagers.userId eq userId }
                .map { it[PetManagers.petId] }
                .distinct()

            petIds.mapNotNull { petId ->
                val petRow = Pets.select { Pets.id eq petId }.singleOrNull()
                if (petRow != null) {
                    val managerIds = PetManagers.select { PetManagers.petId eq petId }.map { it[PetManagers.userId] }
                    toPet(petRow, managerIds)
                } else {
                    null
                }
            }
        }
    }

    suspend fun addManagerToPet(petId: String, userId: String): Boolean {
        return dbQuery {
            val result = PetManagers.insertIgnore {
                it[PetManagers.petId] = petId
                it[PetManagers.userId] = userId
            }
            result.insertedCount > 0
        }
    }

    suspend fun removeManagerFromPet(petId: String, userId: String): Boolean {
        return dbQuery {
            val deletedRows = PetManagers.deleteWhere {
                (PetManagers.petId eq petId) and (PetManagers.userId eq userId)
            }
            deletedRows > 0
        }
    }

    suspend fun updatePetIfManager(petId: String, userId: String, request: UpdatePetRequest): Pet? {
        return dbQuery {
            // verify manager relationship
            val isManager = PetManagers.select {
                (PetManagers.petId eq petId) and (PetManagers.userId eq userId)
            }.any()
            if (!isManager) return@dbQuery null

            val updated = Pets.update({ Pets.id eq petId }) {
                it[name] = request.name
                it[photoUrl] = request.photoUrl
            }
            if (updated == 0) return@dbQuery null

            val petRow = Pets.select { Pets.id eq petId }.singleOrNull() ?: return@dbQuery null
            val managerIds = PetManagers.select { PetManagers.petId eq petId }.map { it[PetManagers.userId] }
            toPet(petRow, managerIds)
        }
    }

    suspend fun deletePetIfManager(petId: String, userId: String): Boolean {
        return dbQuery {
            // verify manager relationship
            val isManager = PetManagers.select {
                (PetManagers.petId eq petId) and (PetManagers.userId eq userId)
            }.any()
            if (!isManager) return@dbQuery false

            // delete pet (will cascade pet_managers via FK if configured at DB level)
            val deleted = Pets.deleteWhere { Pets.id eq petId }
            deleted > 0
        }
    }

    private fun toPet(row: ResultRow, managerIds: List<String>): Pet {
        return Pet(
            id = row[Pets.id],
            name = row[Pets.name],
            photoUrl = row[Pets.photoUrl],
            managingUserIds = managerIds
        )
    }
}
