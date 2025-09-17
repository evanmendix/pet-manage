package com.example.features.feeding

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Query
import com.google.cloud.firestore.QueryDocumentSnapshot
import com.google.firebase.cloud.FirestoreClient
import java.util.UUID

class DuplicateFeedingException(message: String) : Exception(message)

class FeedingService {

    private val db: Firestore by lazy { FirestoreClient.getFirestore() }

    // TODO: The current implementation uses a top-level 'feedings' collection for simplicity.
    // A future refactoring should move this to a sub-collection under a 'pets' collection
    // to support multi-pet and multi-family scenarios, as documented in `doc/api/sd.md`.
    private val feedingsCollection = db.collection("feedings")

    private fun documentToFeeding(doc: QueryDocumentSnapshot): Feeding {
        return Feeding(
            id = doc.getString("id"),
            userId = doc.getString("userId") ?: "",
            timestamp = doc.getLong("timestamp") ?: 0L,
            type = doc.getString("type") ?: "",
            photoUrl = doc.getString("photoUrl")
        )
    }

    fun getFeedings(startTime: Long? = null, endTime: Long? = null): List<Feeding> {
        var query: Query = feedingsCollection.orderBy("timestamp", Query.Direction.DESCENDING)

        if (startTime != null) {
            query = query.whereGreaterThanOrEqualTo("timestamp", startTime)
        }
        if (endTime != null) {
            query = query.whereLessThanOrEqualTo("timestamp", endTime)
        }

        // Apply a default limit if no time range is specified
        if (startTime == null && endTime == null) {
            query = query.limit(30)
        }

        val querySnapshot = query.get().get()
        return querySnapshot.documents.map { documentToFeeding(it) }
    }

    fun addFeeding(feeding: Feeding, force: Boolean = false): Feeding {
        if (feeding.type == "meal" && !force) {
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
        return querySnapshot.documents.firstOrNull()?.let { documentToFeeding(it) }
    }

    fun overwriteLastMeal(feeding: Feeding): Feeding {
        // Find the most recent meal to overwrite
        val query = feedingsCollection
            .whereEqualTo("type", "meal")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
        val querySnapshot = query.get().get()

        if (querySnapshot.isEmpty) {
            // No meal found to overwrite, so just add a new one
            return addFeeding(feeding)
        } else {
            val lastMealDoc = querySnapshot.documents.first()
            val updatedFeeding = feeding.copy(id = lastMealDoc.id)
            lastMealDoc.reference.set(updatedFeeding).get()
            return updatedFeeding
        }
    }
}
