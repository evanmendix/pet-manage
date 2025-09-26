package com.supercatdev.catfeeder.data

import com.supercatdev.catfeeder.data.model.Pet
import com.supercatdev.catfeeder.data.network.PetApiService
import com.supercatdev.catfeeder.data.network.dto.CreatePetRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetRepository @Inject constructor(
    private val petApiService: PetApiService
) {
    suspend fun getPets(): List<Pet> {
        return petApiService.getPets()
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
}
