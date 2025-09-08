package com.example.features.user

import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient

class UserService {

    private val db: Firestore by lazy { FirestoreClient.getFirestore() }

    // TODO: The current implementation uses a top-level 'users' collection for simplicity.
    // A future refactoring should move this to a sub-collection under a 'families' collection
    // to support multi-family scenarios, as documented in `doc/api/sd.md`.
    private val usersCollection = db.collection("users")

    fun createUser(user: User): User {
        usersCollection.document(user.id).set(user).get()
        return user
    }

    fun updateUser(userId: String, user: User): User? {
        // Ensure the ID in the path matches the ID in the body
        if (userId != user.id) {
            throw IllegalArgumentException("User ID in path does not match user ID in body")
        }
        usersCollection.document(userId).set(user, com.google.cloud.firestore.SetOptions.merge()).get()
        return user
    }

    fun getUser(userId: String): User? {
        val document = usersCollection.document(userId).get().get()
        return document.toObject(User::class.java)
    }
}
