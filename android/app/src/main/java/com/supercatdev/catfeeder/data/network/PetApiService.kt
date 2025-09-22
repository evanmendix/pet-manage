package com.supercatdev.catfeeder.data.network

import com.supercatdev.catfeeder.data.model.Pet
import com.supercatdev.catfeeder.data.network.dto.CreatePetRequest
import com.supercatdev.catfeeder.data.network.dto.UpdatePetRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

interface PetApiService {

    @GET("pets")
    suspend fun getPets(): List<Pet>

    // Public all-pets endpoint exposed at /api/v1/pets/all
    @GET("pets/all")
    suspend fun getAllPets(): List<Pet>

    @POST("pets")
    suspend fun createPet(@Body request: CreatePetRequest): Pet

    @POST("pets/{petId}/managers")
    suspend fun addManager(@Path("petId") petId: String): Response<Unit>

    @DELETE("pets/{petId}/managers")
    suspend fun removeManager(@Path("petId") petId: String): Response<Unit>

    @PUT("pets/{petId}")
    suspend fun updatePet(@Path("petId") petId: String, @Body request: UpdatePetRequest): Pet

    @DELETE("pets/{petId}")
    suspend fun deletePet(@Path("petId") petId: String): Response<Unit>
}
