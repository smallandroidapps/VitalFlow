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
import com.location.vitalflow.domain.model.WaterLog
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WaterHistoryScreen(viewModel: WaterViewModel) {
    val logs by viewModel.waterLogs.collectAsState()
    var editingLog by remember { mutableStateOf<WaterLog?>(null) }
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Hydration History",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        val groupedLogs = logs.groupBy { formatDate(it.timestamp) }

        LazyColumn(modifier = Modifier.weight(1f)) {
            groupedLogs.forEach { (date, dailyLogs) ->
                item {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(dailyLogs) { log ->
                    WaterLogItem(
                        log = log,
                        onDelete = { viewModel.deleteWater(it) },
                        onEdit = { editingLog = it }
                    )
                }
            }
        }
    }

    editingLog?.let { log ->
        EditWaterDialog(
            log = log,
            onDismiss = { editingLog = null },
            onConfirm = { updatedLog ->
                viewModel.updateWater(updatedLog)
                editingLog = null
            }
        )
    }
}

@Composable
fun WaterLogItem(log: WaterLog, onDelete: (WaterLog) -> Unit, onEdit: (WaterLog) -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text("${log.amountMl}ml") },
        supportingContent = { Text(formatTime(log.timestamp)) },
        trailingContent = {
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
    )
    HorizontalDivider()
}

@Composable
fun EditWaterDialog(log: WaterLog, onDismiss: () -> Unit, onConfirm: (WaterLog) -> Unit) {
    var amount by remember { mutableStateOf(log.amountMl.toString()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Water Log") },
        text = {
            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (ml)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { 
                amount.toIntOrNull()?.let { onConfirm(log.copy(amountMl = it)) }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

private fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
}

private fun formatTime(timestamp: Long): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
}
