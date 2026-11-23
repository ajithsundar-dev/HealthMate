package com.ajith.healthmate.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ajith.healthmate.data.HealthRecord
import com.ajith.healthmate.viewmodel.HealthViewModel

@Composable
fun RecordListScreen(
    viewModel: HealthViewModel,
    onEdit: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val records by viewModel.records.collectAsStateWithLifecycle()
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()

    var recordToDelete by remember {
        mutableStateOf<HealthRecord?>(null)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Health records",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = searchText,
            onValueChange = viewModel::onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Search by date")
            },
            placeholder = {
                Text("2026-10-25 or 2026-10")
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            viewModel.onSearchChange("")
                        }
                    ) {
                        Text("Clear")
                    }
                }
            },
            singleLine = true
        )

        if (records.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchText.isBlank()) {
                        "No records yet. Add your first entry."
                    } else {
                        "No records match this date."
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                items(
                    items = records,
                    key = { it.id },
                    contentType = { "health_record" }
                ) { record ->
                    HealthRecordCard(
                        record = record,
                        onEdit = {
                            onEdit(record.id)
                        },
                        onDelete = {
                            recordToDelete = record
                        }
                    )
                }
            }
        }
    }

    val pendingRecord = recordToDelete

    if (pendingRecord != null) {
        AlertDialog(
            onDismissRequest = {
                recordToDelete = null
            },
            title = {
                Text("Delete record?")
            },
            text = {
                Text(
                    "Delete the record for ${pendingRecord.date}? " +
                            "This cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteRecord(pendingRecord)
                        recordToDelete = null
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        recordToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun HealthRecordCard(
    record: HealthRecord,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val waterColor = if (isSystemInDarkTheme()) {
        Color(0xFF90CAF9)
    } else {
        Color(0xFF1565C0)
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = record.date,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Steps: ${record.steps}",
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Calories: ${record.calories} kcal",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )

            Text(
                text = "Water: ${record.water} ml",
                style = MaterialTheme.typography.bodyLarge,
                color = waterColor
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onEdit
                ) {
                    Text("Edit")
                }

                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            }
        }
    }
}