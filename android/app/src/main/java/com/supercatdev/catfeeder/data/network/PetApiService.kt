package com.supercatdev.catfeeder.data.network

import com.supercatdev.catfeeder.data.model.Pet
import com.supercatdev.catfeeder.data.network.dto.CreatePetRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PetApiService {

    @GET("pets")
    suspend fun getPets(): List<Pet>

    @POST("pets")
    suspend fun createPet(@Body request: CreatePetRequest): Pet

    @POST("pets/{petId}/managers")
    suspend fun addManager(@Path("petId") petId: String): Response<Unit>

    @DELETE("pets/{petId}/managers")
    suspend fun removeManager(@Path("petId") petId: String): Response<Unit>
}
