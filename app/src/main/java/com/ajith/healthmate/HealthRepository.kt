package com.ajith.healthmate.data

import kotlinx.coroutines.flow.Flow

class HealthRepository(
    private val dao: HealthRecordDao
) {

    val allRecords: Flow<List<HealthRecord>> =
        dao.getAllRecords()

    fun getRecordsByDate(date: String): Flow<List<HealthRecord>> {
        return dao.getRecordsByDate(date)
    }

    fun searchByDate(query: String): Flow<List<HealthRecord>> {
        return dao.searchByDate(query)
    }

    suspend fun getRecordById(id: Int): HealthRecord? {
        return dao.getRecordById(id)
    }

    suspend fun add(record: HealthRecord): Long {
        return dao.insert(record)
    }

    suspend fun update(record: HealthRecord) {
        dao.update(record)
    }

    suspend fun delete(record: HealthRecord) {
        dao.delete(record)
    }

    suspend fun insertAll(records: List<HealthRecord>) {
        dao.insertAll(records)
    }

    suspend fun getCount(): Int {
        return dao.getCount()
    }
}