package com.example.features.feeding

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Query
import com.google.firebase.cloud.FirestoreClient
import java.util.UUID

class FeedingService {

    private val db: Firestore by lazy { FirestoreClient.getFirestore() }

    // TODO: The current implementation uses a top-level 'feedings' collection for simplicity.
    // A future refactoring should move this to a sub-collection under a 'pets' collection
    // to support multi-pet and multi-family scenarios, as documented in `doc/api/sd.md`.
    private val feedingsCollection = db.collection("feedings")

    fun getRecentFeedings(): List<Feeding> {
        val query = feedingsCollection.orderBy("timestamp", Query.Direction.DESCENDING).limit(30)
        val querySnapshot = query.get().get()
        return querySnapshot.documents.mapNotNull { it.toObject(Feeding::class.java) }
    }

    fun addFeeding(feeding: Feeding): Feeding {
        val id = UUID.randomUUID().toString()
        val newFeeding = feeding.copy(id = id)
        feedingsCollection.document(id).set(newFeeding).get()
        return newFeeding
    }

    fun getCurrentStatus(): FeedingStatusResponse {
        val query = feedingsCollection.orderBy("timestamp", Query.Direction.DESCENDING).limit(1)
        val querySnapshot = query.get().get()

        if (querySnapshot.isEmpty) {
            return FeedingStatusResponse(isFed = false, message = "No feeding records found.")
        }

        val lastFeeding = querySnapshot.documents[0].toObject(Feeding::class.java)!!
        val fourHoursInMillis = 4 * 60 * 60 * 1000
        val currentTime = System.currentTimeMillis()

        val isRecentMeal = lastFeeding.type == "meal" && (currentTime - lastFeeding.timestamp) < fourHoursInMillis

        return if (isRecentMeal) {
            // ToDo: Get user name from user service to make the message more informative
            val message = "Warning: A meal was already served recently."
            FeedingStatusResponse(isFed = true, message = message, lastFeeding = lastFeeding)
        } else {
            val message = "It's okay to feed the cat a meal."
            FeedingStatusResponse(isFed = false, message = message, lastFeeding = lastFeeding)
        }
    }
}
