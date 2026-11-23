package com.ajith.healthmate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ajith.healthmate.data.HealthRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    onSave: (HealthRecord) -> Unit,
    onCancel: () -> Unit,
    record: HealthRecord? = null,
    isSaving: Boolean = false,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    var date by rememberSaveable(record?.id) {
        mutableStateOf(
            record?.date ?: SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            ).format(Date())
        )
    }

    var steps by rememberSaveable(record?.id) {
        mutableStateOf(record?.steps?.toString() ?: "")
    }

    var calories by rememberSaveable(record?.id) {
        mutableStateOf(record?.calories?.toString() ?: "")
    }

    var water by rememberSaveable(record?.id) {
        mutableStateOf(record?.water?.toString() ?: "")
    }

    var showErrors by rememberSaveable(record?.id) {
        mutableStateOf(false)
    }

    var showDatePicker by rememberSaveable(record?.id) {
        mutableStateOf(false)
    }

    val utcDateFormat = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    val stepsError = validateHealthNumber(
        steps, "Steps", 100000
    )

    val caloriesError = validateHealthNumber(
        calories, "Calories", 20000
    )

    val waterError = validateHealthNumber(
        water, "Water", 20000
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (record == null) {
                "Add health record"
            } else {
                "Edit health record"
            },
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Choose a date and enter your health details.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "Date",
            style = MaterialTheme.typography.titleSmall
        )

        OutlinedButton(
            onClick = {
                focusManager.clearFocus()
                showDatePicker = true
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select date: $date")
        }

        HealthNumberField(
            label = "Steps",
            value = steps,
            onValueChange = { steps = it },
            maxDigits = 6,
            maximum = 100000,
            error = if (showErrors) stepsError else null,
            enabled = !isSaving
        )

        HealthNumberField(
            label = "Calories (kcal)",
            value = calories,
            onValueChange = { calories = it },
            maxDigits = 5,
            maximum = 20000,
            error = if (showErrors) caloriesError else null,
            enabled = !isSaving
        )

        HealthNumberField(
            label = "Water (ml)",
            value = water,
            onValueChange = { water = it },
            maxDigits = 5,
            maximum = 20000,
            error = if (showErrors) waterError else null,
            enabled = !isSaving
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                enabled = !isSaving,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    showErrors = true
                    focusManager.clearFocus()

                    if (
                        stepsError == null &&
                        caloriesError == null &&
                        waterError == null
                    ) {
                        onSave(
                            HealthRecord(
                                id = record?.id ?: 0,
                                date = date,
                                steps = steps.toInt(),
                                calories = calories.toInt(),
                                water = water.toInt()
                            )
                        )
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = when {
                        isSaving -> "Saving..."
                        record == null -> "Save"
                        else -> "Update"
                    }
                )
            }
        }
    }

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis =
                utcDateFormat.parse(date)?.time
        )

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    enabled = pickerState.selectedDateMillis != null,
                    onClick = {
                        pickerState.selectedDateMillis?.let {
                            date = utcDateFormat.format(Date(it))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = pickerState,
                showModeToggle = false
            )
        }
    }
}

@Composable
private fun HealthNumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    maxDigits: Int,
    maximum: Int,
    error: String?,
    enabled: Boolean
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            if (
                input.length <= maxDigits &&
                input.all { it in '0'..'9' }
            ) {
                onValueChange(input)
            }
        },
        label = {
            Text(label)
        },
        supportingText = {
            Text(error ?: "Enter a whole number from 0 to $maximum.")
        },
        isError = error != null,
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

private fun validateHealthNumber(
    value: String,
    label: String,
    maximum: Int
): String? {
    if (value.isBlank()) {
        return "$label is required."
    }

    val number = value.toIntOrNull()
        ?: return "Enter a valid whole number."

    if (number !in 0..maximum) {
        return "$label must be between 0 and $maximum."
    }

    return null
}