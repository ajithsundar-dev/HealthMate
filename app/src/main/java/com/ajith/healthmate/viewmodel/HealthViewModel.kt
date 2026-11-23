package com.ajith.healthmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajith.healthmate.data.DummyData
import com.ajith.healthmate.data.HealthRecord
import com.ajith.healthmate.data.HealthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TodaySummary(
    val steps: Int = 0,
    val calories: Int = 0,
    val water: Int = 0,
    val recordCount: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
class HealthViewModel(
    private val repository: HealthRepository
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _messages =
        MutableSharedFlow<String>(extraBufferCapacity = 1)
    val messages = _messages.asSharedFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _saveCompleted = MutableStateFlow(false)
    val saveCompleted = _saveCompleted.asStateFlow()

    val records: StateFlow<List<HealthRecord>> =
        _searchText
            .flatMapLatest { query ->
                repository.searchByDate(query.trim())
                    .catch {
                        _messages.emit("Unable to load records.")
                        emit(emptyList())
                    }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val todaySummary: StateFlow<TodaySummary> =
        repository.allRecords
            .map { records ->
                val today = SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.US
                ).format(Date())

                val todayRecords = records.filter {
                    it.date == today
                }

                TodaySummary(
                    steps = todayRecords.sumOf { it.steps },
                    calories = todayRecords.sumOf { it.calories },
                    water = todayRecords.sumOf { it.water },
                    recordCount = todayRecords.size
                )
            }
            .catch {
                _messages.emit("Unable to load today's summary.")
                emit(TodaySummary())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = TodaySummary()
            )

    init {
        runOperation {
            DummyData.populateIfEmpty(repository)
        }
    }

    fun onSearchChange(query: String) {
        _searchText.value = query
    }

    suspend fun getRecord(id: Int): HealthRecord? {
        return repository.getRecordById(id)
    }

    fun consumeSaveCompleted() {
        _saveCompleted.value = false
    }

    fun addRecord(record: HealthRecord) {
        runOperation(
            successMessage = "Record added successfully",
            recordSave = true
        ) {
            repository.add(record)
        }
    }

    fun updateRecord(record: HealthRecord) {
        runOperation(
            successMessage = "Record updated successfully",
            recordSave = true
        ) {
            repository.update(record)
        }
    }

    fun deleteRecord(record: HealthRecord) {
        runOperation("Record deleted successfully") {
            repository.delete(record)
        }
    }

    private fun runOperation(
        successMessage: String? = null,
        recordSave: Boolean = false,
        action: suspend () -> Unit
    ) {
        if (recordSave) {
            if (_isSaving.value || _saveCompleted.value) {
                return
            }
            _isSaving.value = true
        }

        viewModelScope.launch {
            try {
                action()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _messages.emit(
                    "Unable to complete the action. Please try again."
                )
                return@launch
            } finally {
                if (recordSave) {
                    _isSaving.value = false
                }
            }

            if (recordSave) {
                _saveCompleted.value = true
            }

            if (successMessage != null) {
                _messages.emit(successMessage)
            }
        }
    }
}