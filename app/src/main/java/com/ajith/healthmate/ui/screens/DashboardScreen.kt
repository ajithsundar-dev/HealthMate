package com.ajith.healthmate.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ajith.healthmate.viewmodel.HealthViewModel

@Composable
fun DashboardScreen(
    viewModel: HealthViewModel,
    onAddRecord: () -> Unit,
    modifier: Modifier = Modifier
) {
    val summary by viewModel.todaySummary.collectAsStateWithLifecycle()

    val darkTheme = isSystemInDarkTheme()

    val stepsColor = if (darkTheme) {
        Color(0xFFA5D6A7)
    } else {
        Color(0xFF2E7D32)
    }

    val waterColor = if (darkTheme) {
        Color(0xFF90CAF9)
    } else {
        Color(0xFF1565C0)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Today's overview",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Track your steps, calories, and water intake.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        SummaryCard(
            title = "Steps",
            value = "${summary.steps}",
            color = stepsColor
        )

        SummaryCard(
            title = "Calories",
            value = "${summary.calories} kcal",
            color = MaterialTheme.colorScheme.error
        )

        SummaryCard(
            title = "Water",
            value = "${summary.water} ml",
            color = waterColor
        )

        if (summary.recordCount == 0) {
            Text(
                text = "No records for today yet. Add your first entry.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            val entryLabel = if (summary.recordCount == 1) {
                "entry"
            } else {
                "entries"
            }

            Text(
                text = "Based on ${summary.recordCount} $entryLabel today.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Button(
            onClick = onAddRecord,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add health record")
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.12f),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = color
            )
        }
    }
}