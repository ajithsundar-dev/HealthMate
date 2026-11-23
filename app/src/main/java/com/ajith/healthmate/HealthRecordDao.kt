package com.ajith.healthmate.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthRecordDao {

    @Insert
    suspend fun insert(record: HealthRecord): Long

    @Insert
    suspend fun insertAll(records: List<HealthRecord>)

    @Query("SELECT * FROM health_records ORDER BY date DESC, id DESC")
    fun getAllRecords(): Flow<List<HealthRecord>>

    @Query("SELECT * FROM health_records WHERE id = :id")
    suspend fun getRecordById(id: Int): HealthRecord?

    @Query(
        "SELECT * FROM health_records WHERE date = :date ORDER BY id DESC"
    )
    fun getRecordsByDate(date: String): Flow<List<HealthRecord>>

    @Query(
        """
        SELECT * FROM health_records
        WHERE date LIKE '%' || :query || '%'
        ORDER BY date DESC, id DESC
        """
    )
    fun searchByDate(query: String): Flow<List<HealthRecord>>

    @Update
    suspend fun update(record: HealthRecord)

    @Delete
    suspend fun delete(record: HealthRecord)

    @Query("SELECT COUNT(*) FROM health_records")
    suspend fun getCount(): Int
}