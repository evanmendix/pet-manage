package com.example.core

import com.example.features.feeding.Feedings
import com.example.features.pet.PetManagers
import com.example.features.pet.Pets
import com.example.features.user.Users
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        val driverClassName = "org.postgresql.Driver"
        val jdbcURL = System.getenv("DB_JDBC_URL") ?: "jdbc:postgresql://localhost:5071/pet_feeder_db"
        val user = System.getenv("DB_USER") ?: "root"
        val password = System.getenv("DB_PASSWORD") ?: "secret"

        try {
            println("DATABASE: Connecting to $jdbcURL")
            val database = Database.connect(jdbcURL, driverClassName, user, password)
            println("DATABASE: Connection successful.")
            transaction(database) {
                addLogger(StdOutSqlLogger)
                println("DATABASE: Initializing schema...")
                SchemaUtils.createMissingTablesAndColumns(Users, Pets, PetManagers, Feedings)
                println("DATABASE: Schema initialization complete.")
            }
        } catch (e: Exception) {
            println("DATABASE: Initialization failed!")
            e.printStackTrace()
        }
    }

    /**
     * A helper function to execute a database query in a suspended transaction.
     * This ensures that database operations are run on a dedicated thread pool (Dispatchers.IO)
     * to avoid blocking the main application threads.
     *
     * @param block The database operation to be executed.
     * @return The result of the database operation.
     */
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
