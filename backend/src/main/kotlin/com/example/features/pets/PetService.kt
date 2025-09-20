package com.example.features.pets

import com.example.core.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll

class PetService {

    suspend fun getAll(): List<Pet> = dbQuery {
        Pets.selectAll().map(::resultRowToPet)
    }

    private fun resultRowToPet(row: ResultRow) = Pet(
        id = row[Pets.id],
        name = row[Pets.name],
        photoUrl = row[Pets.photoUrl]
    )
}
