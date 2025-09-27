package com.example.features.feeding

import com.example.core.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class DuplicateFeedingException(message: String) : Exception(message)

class FeedingService {

    suspend fun getFeedings(petId: String, startTime: Long?, endTime: Long?): List<Feeding> {
        return dbQuery {
            val query = Feedings.select { Feedings.petId eq petId }.orderBy(Feedings.timestamp, SortOrder.DESC)
            startTime?.let { query.andWhere { Feedings.timestamp greaterEq it } }
            endTime?.let { query.andWhere { Feedings.timestamp lessEq it } }

            // Apply a default limit if no time range is specified
            if (startTime == null && endTime == null) {
                query.limit(30)
            }

            query.map { toFeeding(it) }
        }
    }

    suspend fun addFeeding(userId: String, request: CreateFeedingRequest): Feeding {
        if (request.type == "meal" && !request.force) {
            val fourHoursInMillis = 4 * 60 * 60 * 1000
            val windowStart = request.timestamp - fourHoursInMillis
            val recentMeal = dbQuery {
                Feedings.select {
                    (Feedings.petId eq request.petId) and
                            (Feedings.type eq "meal") and
                            (Feedings.timestamp greaterEq windowStart) and
                            (Feedings.timestamp less request.timestamp)
                }.limit(1).singleOrNull()
            }
            if (recentMeal != null) {
                throw DuplicateFeedingException("A meal has already been recorded in the last 4 hours.")
            }
        }

        val newFeeding = Feeding(
            id = UUID.randomUUID().toString(),
            userId = userId,
            petId = request.petId,
            timestamp = request.timestamp,
            type = request.type,
            photoUrl = request.photoUrl
        )

        dbQuery {
            Feedings.insert {
                it[id] = newFeeding.id
                it[Feedings.userId] = newFeeding.userId
                it[petId] = newFeeding.petId
                it[timestamp] = newFeeding.timestamp
                it[type] = newFeeding.type
                it[photoUrl] = newFeeding.photoUrl
            }
        }
        return newFeeding
    }

    suspend fun getCurrentStatus(petId: String): Feeding? {
        return dbQuery {
            Feedings.select { Feedings.petId eq petId }
                .orderBy(Feedings.timestamp, SortOrder.DESC)
                .limit(1)
                .map { toFeeding(it) }
                .singleOrNull()
        }
    }

    suspend fun overwriteLastMeal(userId: String, request: OverwriteMealRequest): Feeding {
        val lastMeal = dbQuery {
            Feedings.select { (Feedings.petId eq request.petId) and (Feedings.type eq "meal") }
                .orderBy(Feedings.timestamp, SortOrder.DESC)
                .limit(1)
                .singleOrNull()
        }

        val feedingToSave = Feeding(
            id = lastMeal?.get(Feedings.id) ?: UUID.randomUUID().toString(),
            userId = userId,
            petId = request.petId,
            timestamp = request.timestamp,
            type = request.type,
            photoUrl = request.photoUrl
        )

        dbQuery {
            Feedings.upsert {
                it[id] = feedingToSave.id
                it[Feedings.userId] = feedingToSave.userId
                it[petId] = feedingToSave.petId
                it[timestamp] = feedingToSave.timestamp
                it[type] = feedingToSave.type
                it[photoUrl] = feedingToSave.photoUrl
            }
        }
        return feedingToSave
    }

    private fun toFeeding(row: ResultRow) = Feeding(
        id = row[Feedings.id],
        userId = row[Feedings.userId],
        petId = row[Feedings.petId],
        timestamp = row[Feedings.timestamp],
        type = row[Feedings.type],
        photoUrl = row[Feedings.photoUrl]
    )
}
