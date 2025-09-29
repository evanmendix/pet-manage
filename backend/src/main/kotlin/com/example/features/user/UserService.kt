package com.example.features.user

import com.example.features.pet.Pet
import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import com.example.core.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import io.ktor.http.content.*
import java.io.File

class UserService {

    private val db: Firestore by lazy { FirestoreClient.getFirestore() }

    private val familiesCollection = db.collection("families")

    /**
     * Creates a new user and a corresponding family structure.
     * This function is intended for the initial user onboarding.
     * When a user is created, a new family is created for them, and a default pet is added to that family.
     */
    fun createUser(uid: String, request: CreateUserRequest): User {
        val newFamilyRef = familiesCollection.document() // Create a new family with an auto-generated ID
        val newUserRef = newFamilyRef.collection("users").document(uid)
        val newPetRef = newFamilyRef.collection("pets").document() // Create a new pet with an auto-generated ID

        val user = User(
            id = uid,
            name = request.name,
            profilePictureUrl = request.profilePictureUrl
        )

        // The pet's name and photo can be made configurable in a future version.
        val pet = Pet(
            id = newPetRef.id,
            name = "Mimi", // Default pet name
            photoUrl = "https://example.com/default-pet-avatar.png", // Placeholder photo
            managingUserIds = listOf(uid) // The creator automatically manages the default pet.
        )

        // Run the creation of the family, user, and pet within a single transaction
        // to ensure data consistency.
        db.runTransaction { transaction ->
            transaction.set(newFamilyRef, mapOf("createdAt" to System.currentTimeMillis()))
            transaction.set(newUserRef, user)
            transaction.set(newPetRef, pet)
            null // Firestore SDK for Java requires a return value, null for success.
        }.get()

        return user
    }

    /**
     * Finds a user by their ID across all families using a collection group query.
     */
    fun getUser(userId: String): User? {
        val query = db.collectionGroup("users").whereEqualTo("id", userId).limit(1)
        val future = query.get()
        val querySnapshot = future.get()

        if (querySnapshot.isEmpty) {
            return null
        }
        return querySnapshot.documents.first().toObject(User::class.java)
    }

    /**
     * Updates a user's data. It first finds the user via a collection group query
     * and then updates their document.
     */
    fun updateUser(userId: String, request: UpdateUserRequest): User? {
        val query = db.collectionGroup("users").whereEqualTo("id", userId).limit(1)
        val future = query.get()
        val querySnapshot = future.get()

        if (querySnapshot.isEmpty) {
            return null
        }

        val userDocRef = querySnapshot.documents.first().reference
        // Use a map to only update the fields provided in the request.
        val updates = mutableMapOf<String, Any>()
        request.name?.let { updates["name"] = it }
        request.profilePictureUrl?.let { updates["profilePictureUrl"] = it }

        if (updates.isNotEmpty()) {
             userDocRef.update(updates).get()
        }

        // Return the updated user data
        val updatedDoc = userDocRef.get().get()
        return updatedDoc.toObject(User::class.java)
    }

    /**
     * Finds multiple users by their IDs using PostgreSQL `users` table (Exposed).
     */
    fun getUsers(userIds: List<String>): List<User> {
        if (userIds.isEmpty()) return emptyList()

        val distinctIds = userIds.distinct()
        return kotlinx.coroutines.runBlocking {
            dbQuery {
                Users
                    .slice(Users.id, Users.name, Users.profilePictureUrl)
                    .select { Users.id inList distinctIds }
                    .map { row ->
                        User(
                            id = row[Users.id],
                            name = row[Users.name],
                            profilePictureUrl = row[Users.profilePictureUrl]
                        )
                    }
            }
        }
    }

    /**
     * Handles the upload of a user's profile picture.
     * This function saves the file to a designated storage location and updates the user's record
     * in the PostgreSQL database with the URL of the new picture.
     */
    suspend fun uploadProfilePicture(userId: String, fileItem: PartData.FileItem): String? {
        // Define the base storage directory. This path corresponds to the volume mount in docker-compose.yml.
        val storagePath = "/storage/users/profile"
        val profileDir = File(storagePath)

        // Create the directory if it doesn't exist.
        if (!profileDir.exists()) {
            profileDir.mkdirs()
        }

        // Generate a unique filename using the user's ID to ensure one profile picture per user.
        val fileExtension = File(fileItem.originalFileName ?: "unknown").extension
        val newFileName = "$userId.$fileExtension"
        val file = File(profileDir, newFileName)

        // Save the uploaded file.
        fileItem.streamProvider().use { input ->
            file.outputStream().buffered().use { output ->
                input.copyTo(output)
            }
        }

        // Construct the relative URL path to be stored in the database.
        val fileUrl = "/users/profile/$newFileName"

        // Update the user's profilePictureUrl in the PostgreSQL database.
        val rowsUpdated = dbQuery {
            Users.update({ Users.id eq userId }) {
                it[profilePictureUrl] = fileUrl
            }
        }

        return if (rowsUpdated > 0) {
            fileUrl
        } else {
            // Optionally handle the case where the user ID does not exist in the database.
            // For now, returning null indicates failure.
            null
        }
    }
}
