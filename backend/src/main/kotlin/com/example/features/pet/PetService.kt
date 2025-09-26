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
            // Use a transaction to ensure both inserts succeed or fail together
            transaction {
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
        }
        return newPet
    }

    suspend fun getAllPets(): List<Pet> {
        return dbQuery {
            val allPets = Pets.selectAll().toList()
            val allManagers = PetManagers.selectAll().toList()

            allPets.map { petRow ->
                val managerIds = allManagers
                    .filter { it[PetManagers.petId] == petRow[Pets.id] }
                    .map { it[PetManagers.userId] }
                toPet(petRow, managerIds)
            }
        }
    }

    suspend fun deletePet(petId: String): Boolean = dbQuery {
        transaction {
            PetManagers.deleteWhere { PetManagers.petId eq petId }
            Pets.deleteWhere { Pets.id eq petId } > 0
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

    private fun toPet(row: ResultRow, managerIds: List<String>): Pet {
        return Pet(
            id = row[Pets.id],
            name = row[Pets.name],
            photoUrl = row[Pets.photoUrl],
            managingUserIds = managerIds
        )
    }
}
