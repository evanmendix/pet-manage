package com.example.features.pet

import com.google.cloud.firestore.FieldValue
import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient

class PetService {

    private val db: Firestore by lazy { FirestoreClient.getFirestore() }

    /**
     * Finds the family document reference for a given user ID.
     * This is a crucial helper function to locate the correct 'pets' sub-collection.
     */
    private fun getFamilyRefForUser(userId: String): com.google.cloud.firestore.DocumentReference? {
        val userQuery = db.collectionGroup("users").whereEqualTo("id", userId).limit(1).get().get()
        if (userQuery.isEmpty) {
            return null
        }
        // The user document's parent is the family document.
        return userQuery.documents.first().reference.parent.parent
    }

    /**
     * Finds a pet document reference by its ID across all families.
     */
    private fun getPetRef(petId: String): com.google.cloud.firestore.DocumentReference? {
        val petQuery = db.collectionGroup("pets").whereEqualTo("id", petId).limit(1).get().get()
        if (petQuery.isEmpty) {
            return null
        }
        return petQuery.documents.first().reference
    }

    /**
     * Creates a new pet for the given user's family.
     */
    fun createPet(userId: String, name: String, photoUrl: String?): Pet? {
        val familyRef = getFamilyRefForUser(userId) ?: return null
        val newPetRef = familyRef.collection("pets").document()
        val newPet = Pet(
            id = newPetRef.id,
            name = name,
            photoUrl = photoUrl,
            managingUserIds = listOf(userId) // The creator automatically manages the new pet.
        )
        newPetRef.set(newPet).get()
        return newPet
    }

    /**
     * Retrieves all pets for a given user's family.
     */
    fun getPetsForUserFamily(userId: String): List<Pet>? {
        val familyRef = getFamilyRefForUser(userId) ?: return null
        val petsQuery = familyRef.collection("pets").get().get()
        return petsQuery.documents.mapNotNull { it.toObject(Pet::class.java) }
    }

    /**
     * Adds a user to a pet's list of managers.
     */
    fun addManagerToPet(petId: String, userId: String): Boolean {
        val petRef = getPetRef(petId) ?: return false
        petRef.update("managingUserIds", FieldValue.arrayUnion(userId)).get()
        return true
    }

    /**
     * Removes a user from a pet's list of managers.
     */
    fun removeManagerFromPet(petId: String, userId: String): Boolean {
        val petRef = getPetRef(petId) ?: return false
        petRef.update("managingUserIds", FieldValue.arrayRemove(userId)).get()
        return true
    }
}
