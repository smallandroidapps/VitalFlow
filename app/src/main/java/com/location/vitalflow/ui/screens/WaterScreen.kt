package com.location.vitalflow.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.location.vitalflow.ui.components.WaterWaveCanvas

@Composable
fun WaterScreen(viewModel: WaterViewModel, onNavigateToHistory: () -> Unit) {
    val totalMl by viewModel.dailyTotalMl.collectAsState()
    val waterLogs by viewModel.waterLogs.collectAsState()
    val goalMl by viewModel.dailyGoalMl.collectAsState()
    val velocity by viewModel.hydrationVelocity.collectAsState()
    val fillLevel = (totalMl.toFloat() / goalMl.coerceAtLeast(1)).coerceIn(0f, 1f)
    
    var showGoalDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Hydration",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(24.dp)
                .clip(MaterialTheme.shapes.extraLarge)
                .clickable { onNavigateToHistory() },
            contentAlignment = Alignment.Center
        ) {
            WaterWaveCanvas(
                modifier = Modifier.size(300.dp),
                fillLevel = fillLevel
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(fillLevel * 100).toInt()}%",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${totalMl} / ${goalMl}ml",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.clickable { showGoalDialog = true }
                )
                Text(
                    text = "Tap for History",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Velocity: $velocity",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (velocity == "Optimal") Color(0xFF4CAF50) else Color(0xFFF44336)
                )
            }
        }

        // Custom Analytics Chart
        val trendData = waterLogs.take(7).map { it.amountMl.toFloat() }.reversed()
        if (trendData.isNotEmpty()) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Weekly Activity", style = MaterialTheme.typography.titleSmall)
                Canvas(modifier = Modifier.height(80.dp).fillMaxWidth().padding(16.dp)) {
                    val maxVal = trendData.maxOrNull()?.coerceAtLeast(500f) ?: 500f
                    val space = size.width / (trendData.size - 1).coerceAtLeast(1)
                    trendData.forEachIndexed { i, valMl ->
                        val x = i * space
                        val y = size.height - (valMl / maxVal) * size.height
                        drawCircle(color = Color(0xFF2196F3), radius = 4.dp.toPx(), center = Offset(x, y))
                        if (i > 0) {
                            val prevX = (i - 1) * space
                            val prevY = size.height - (trendData[i-1] / maxVal) * size.height
                            drawLine(color = Color(0xFF2196F3), start = Offset(prevX, prevY), end = Offset(x, y), strokeWidth = 2.dp.toPx())
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickLogButton(100) { viewModel.logWater(100) }
            QuickLogButton(250) { viewModel.logWater(250) }
            QuickLogButton(500) { viewModel.logWater(500) }
        }
    }

    if (showGoalDialog) {
        EditGoalDialog(
            currentGoal = goalMl,
            onDismiss = { showGoalDialog = false },
            onConfirm = { 
                viewModel.setDailyGoal(it)
                showGoalDialog = false
            }
        )
    }
}

@Composable
fun EditGoalDialog(currentGoal: Int, onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var goal by remember { mutableStateOf(currentGoal.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Daily Hydration Goal") },
        text = {
            Column {
                OutlinedTextField(
                    value = goal,
                    onValueChange = { goal = it },
                    label = { Text("Goal (ml)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { goal.toIntOrNull()?.let { onConfirm(it) } }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun QuickLogButton(amount: Int, onClick: () -> Unit) {
    FilledTonalButton(onClick = onClick) {
        Text("+${amount}ml")
    }
}
