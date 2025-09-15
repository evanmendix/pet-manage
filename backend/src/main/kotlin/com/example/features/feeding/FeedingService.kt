package com.example.features.feeding

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Query
import com.google.firebase.cloud.FirestoreClient
import java.util.UUID

class DuplicateFeedingException(message: String) : Exception(message)

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
        if (feeding.type == "meal") {
            // Check for recent meals within the last 4 hours.
            val fourHoursInMillis = 4 * 60 * 60 * 1000
            val windowStart = feeding.timestamp - fourHoursInMillis

            val query = feedingsCollection
                .whereEqualTo("type", "meal")
                .whereGreaterThanOrEqualTo("timestamp", windowStart)
                .whereLessThan("timestamp", feeding.timestamp) // To avoid comparing the event with itself
                .limit(1)

            val querySnapshot = query.get().get()

            if (!querySnapshot.isEmpty) {
                throw DuplicateFeedingException("A meal has already been recorded in the last 4 hours.")
            }
        }

        val id = UUID.randomUUID().toString()
        val newFeeding = feeding.copy(id = id)
        feedingsCollection.document(id).set(newFeeding).get()
        return newFeeding
    }

    fun getCurrentStatus(): Feeding? {
        val query = feedingsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
        val querySnapshot = query.get().get()
        return querySnapshot.documents.firstOrNull()?.toObject(Feeding::class.java)
    }
}
