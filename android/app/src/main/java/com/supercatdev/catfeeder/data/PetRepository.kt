package com.supercatdev.catfeeder.data

import com.supercatdev.catfeeder.data.model.Pet
import com.supercatdev.catfeeder.data.network.PetApiService
import com.supercatdev.catfeeder.data.network.dto.CreatePetRequest
import com.supercatdev.catfeeder.data.network.dto.UpdatePetRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetRepository @Inject constructor(
    private val petApiService: PetApiService
) {
    suspend fun getAllPetsOrManaged(): List<Pet> {
        // Prefer public all-pets endpoint; fallback to managed-only if not available
        return try {
            petApiService.getAllPets()
        } catch (e: Exception) {
            petApiService.getPets()
        }
    }

    suspend fun createPet(createPetRequest: CreatePetRequest): Pet {
        return petApiService.createPet(createPetRequest)
    }

    suspend fun addManager(petId: String) {
        val response = petApiService.addManager(petId)
        if (!response.isSuccessful) {
            // In a real app, we would handle the error more gracefully
            throw Exception("API call to add manager failed with code: ${response.code()}")
        }
    }

    suspend fun removeManager(petId: String) {
        val response = petApiService.removeManager(petId)
        if (!response.isSuccessful) {
            throw Exception("API call to remove manager failed with code: ${response.code()}")
        }
    }

    suspend fun updatePet(petId: String, request: UpdatePetRequest): Pet {
        return petApiService.updatePet(petId, request)
    }

    suspend fun deletePet(petId: String) {
        val response = petApiService.deletePet(petId)
        if (!response.isSuccessful) {
            throw Exception("API call to delete pet failed with code: ${response.code()}")
        }
    }
}
