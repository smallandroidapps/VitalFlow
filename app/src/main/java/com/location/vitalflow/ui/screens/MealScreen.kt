package com.location.vitalflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.location.vitalflow.domain.model.MealLog
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MealScreen(viewModel: MealViewModel) {
    val mealLogs by viewModel.mealLogs.collectAsState()
    val regularity by viewModel.regularityScore.collectAsState()
    val totalCalories by viewModel.dailyCalories.collectAsState()
    val totalProtein by viewModel.dailyProtein.collectAsState()
    var editingLog by remember { mutableStateOf<MealLog?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Meal Reminders",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Regularity: $regularity",
            style = MaterialTheme.typography.labelLarge,
            color = if (regularity == "Excellent") androidx.compose.ui.graphics.Color(0xFF4CAF50) else androidx.compose.ui.graphics.Color(0xFFF44336)
        )
        
        Row(modifier = Modifier.padding(top = 8.dp)) {
            Text(text = "Today: $totalCalories kcal", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "$totalProtein g Protein", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Quick Log Meal", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MealButton("Breakfast") { viewModel.logMeal("BREAKFAST", "ATE") }
            MealButton("Lunch") { viewModel.logMeal("LUNCH", "ATE") }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MealButton("Snack") { viewModel.logMeal("SNACK", "ATE") }
            MealButton("Dinner") { viewModel.logMeal("DINNER", "ATE") }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Meal History", style = MaterialTheme.typography.titleMedium)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(mealLogs) { log ->
                MealLogItem(
                    log = log,
                    onDelete = { viewModel.deleteMeal(it) },
                    onEdit = { editingLog = it }
                )
            }
        }
    }

    editingLog?.let { log ->
        EditMealDialog(
            log = log,
            onDismiss = { editingLog = null },
            onConfirm = { updatedLog ->
                viewModel.updateMeal(updatedLog)
                editingLog = null
            }
        )
    }
}

@Composable
fun MealLogItem(log: MealLog, onDelete: (MealLog) -> Unit, onEdit: (MealLog) -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(log.mealType) },
        supportingContent = { 
            Column {
                Text(formatTime(log.timestamp))
                if (log.calories != null || log.protein != null) {
                    Text(
                        text = "Macros: ${log.calories ?: 0} kcal | ${log.protein ?: 0}g P",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(log.status, style = MaterialTheme.typography.labelSmall)
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = false
                                onEdit(log)
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showMenu = false
                                onDelete(log)
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) }
                        )
                    }
                }
            }
        }
    )
    HorizontalDivider()
}

@Composable
fun EditMealDialog(log: MealLog, onDismiss: () -> Unit, onConfirm: (MealLog) -> Unit) {
    var selectedStatus by remember { mutableStateOf(log.status) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Meal Log") },
        text = {
            Column {
                Text("Type: ${log.mealType}")
                Spacer(modifier = Modifier.height(8.dp))
                listOf("ATE", "SKIPPED", "SNOOZED").forEach { status ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status }
                        )
                        Text(status)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(log.copy(status = selectedStatus)) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun MealButton(label: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.width(140.dp)) {
        Text(label)
    }
}

private fun formatTime(timestamp: Long): String {
    return SimpleDateFormat("HH:mm, MMM dd", Locale.getDefault()).format(Date(timestamp))
}
