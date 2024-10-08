package org.omsi.demoproject.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import org.omsi.demoproject.models.Parameter

@Dao
interface ParameterDao {

    /*Notice, all functions are suspend functions and does not return Flow type here.
    Using Coroutines flow is a good practice but that does not lets you compile and target for all the targets.
     */

    @Upsert
    suspend fun insert(parameter: Parameter): Long

    @Query("SELECT * FROM parameter WHERE id = :id")
    suspend fun getParameterById(id: Int): Parameter?

    @Delete
    suspend fun delete(parameter: Parameter)

    @Query("DELETE FROM parameter WHERE id = :id")
    suspend fun deleteById(id: Int): Int

    @Query("SELECT * FROM parameter")
    suspend fun getAllParameters(): List<Parameter>
}