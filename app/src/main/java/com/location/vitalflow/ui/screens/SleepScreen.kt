package com.location.vitalflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.location.vitalflow.domain.model.SleepLog
import com.location.vitalflow.ui.components.SleepWindowCard
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepScreen(viewModel: SleepViewModel) {
    val sleepLogs by viewModel.sleepLogs.collectAsState()
    val sleepDebt by viewModel.sleepDebt.collectAsState()
    var editingLog by remember { mutableStateOf<SleepLog?>(null) }
    var showCustomNapDialog by remember { mutableStateOf(false) }
    
    val startHour by viewModel.sleepStartHour.collectAsState()
    val endHour by viewModel.sleepEndHour.collectAsState()
    
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Rest & Recovery",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        SleepWindowCard(
            startHour = startHour,
            endHour = endHour,
            onEditStart = { showStartTimePicker = true },
            onEditEnd = { showEndTimePicker = true }
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Sleep Debt (7d): $sleepDebt",
            style = MaterialTheme.typography.bodyMedium,
            color = if (sleepDebt == "Well Rested") Color(0xFF4CAF50) else Color(0xFFF44336)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Quick Log Nap", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NapChip("20m") { viewModel.logCustomNap(20) }
            NapChip("45m") { viewModel.logCustomNap(45) }
            NapChip("Custom") { showCustomNapDialog = true }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Recent Activity", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(sleepLogs) { log ->
                SleepLogItem(
                    log = log,
                    onEdit = { editingLog = it },
                    onDelete = { viewModel.deleteSleep(it) }
                )
            }
        }
    }

    if (showCustomNapDialog) {
        CustomNapDialog(
            onDismiss = { showCustomNapDialog = false },
            onConfirm = { minutes ->
                viewModel.logCustomNap(minutes)
                showCustomNapDialog = false
            }
        )
    }

    if (showStartTimePicker) {
        SleepTimePickerDialog(
            initialHour = startHour,
            onDismiss = { showStartTimePicker = false },
            onConfirm = { hour ->
                viewModel.setSleepWindow(hour, endHour)
                showStartTimePicker = false
            }
        )
    }

    if (showEndTimePicker) {
        SleepTimePickerDialog(
            initialHour = endHour,
            onDismiss = { showEndTimePicker = false },
            onConfirm = { hour ->
                viewModel.setSleepWindow(startHour, hour)
                showEndTimePicker = false
            }
        )
    }

    editingLog?.let { log ->
        EditSleepDialog(
            log = log,
            onDismiss = { editingLog = null },
            onConfirm = { updatedLog ->
                viewModel.updateSleep(updatedLog)
                editingLog = null
            }
        )
    }
}

@Composable
fun CustomNapDialog(onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var minutes by remember { mutableStateOf("30") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Custom Nap Duration") },
        text = {
            OutlinedTextField(
                value = minutes,
                onValueChange = { minutes = it },
                label = { Text("Minutes") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { minutes.toIntOrNull()?.let { onConfirm(it) } }) {
                Text("Log")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepTimePickerDialog(initialHour: Int, onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    val state = rememberTimePickerState(initialHour = initialHour, initialMinute = 0, is24Hour = true)
    
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier.width(IntrinsicSize.Min).height(IntrinsicSize.Min).background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Select Hour", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(24.dp))
                TimePicker(state = state)
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = { onConfirm(state.hour) }) { Text("OK") }
                }
            }
        }
    }
}

@Composable
fun SleepLogItem(log: SleepLog, onEdit: (SleepLog) -> Unit, onDelete: (SleepLog) -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = if (log.type == "NAP") "Nap" else "Sleep", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                Text(text = formatTime(log.startTime), style = MaterialTheme.typography.titleMedium)
                log.endTime?.let {
                    Text(text = "Duration: ${formatDuration(it - log.startTime)}", style = MaterialTheme.typography.bodySmall)
                }
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Actions")
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            showMenu = false
                            onEdit(log)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            showMenu = false
                            onDelete(log)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EditSleepDialog(log: SleepLog, onDismiss: () -> Unit, onConfirm: (SleepLog) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modify Log") },
        text = {
            Column {
                Text("Change type from ${log.type}?")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isSleep = log.type == "SLEEP"
                    RadioButton(selected = isSleep, onClick = { onConfirm(log.copy(type = "SLEEP")) })
                    Text("Sleep")
                    Spacer(Modifier.width(16.dp))
                    RadioButton(selected = !isSleep, onClick = { onConfirm(log.copy(type = "NAP")) })
                    Text("Nap")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun NapChip(label: String, onClick: () -> Unit) {
    SuggestionChip(onClick = onClick, label = { Text(label) })
}

private fun formatTime(timestamp: Long): String {
    return SimpleDateFormat("HH:mm, MMM dd", Locale.getDefault()).format(Date(timestamp))
}

private fun formatDuration(millis: Long): String {
    val minutes = (millis / (1000 * 60)) % 60
    val hours = (millis / (1000 * 60 * 60))
    return "${hours}h ${minutes}m"
}
