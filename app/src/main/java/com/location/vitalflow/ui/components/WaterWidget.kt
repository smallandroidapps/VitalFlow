package com.location.vitalflow.ui.components

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.GlanceTheme
import androidx.glance.text.FontWeight
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.unit.ColorProvider
import androidx.compose.ui.graphics.Color
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.background
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback

class WaterWidget : GlanceAppWidget() {
    
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetContent()
            }
        }
    }

    @androidx.compose.runtime.Composable
    private fun WidgetContent() {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .padding(8.dp),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = "VitalFlow",
                style = TextStyle(fontWeight = FontWeight.Bold, color = GlanceTheme.colors.onSurface)
            )
            Spacer(modifier = GlanceModifier.height(8.dp))
            Text(text = "Stay Hydrated!", style = TextStyle(color = GlanceTheme.colors.onSurfaceVariant))
            Spacer(modifier = GlanceModifier.height(16.dp))
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {
                Button(
                    text = "+250ml",
                    onClick = actionRunCallback<LogWaterWidgetCallback>(
                        actionParametersOf(AmountKey to 250)
                    )
                )
            }
        }
    }

    companion object {
        val AmountKey = ActionParameters.Key<Int>("amount")
    }
}

class LogWaterWidgetCallback : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val amount = parameters[WaterWidget.AmountKey] ?: 250
        
        // We'd ideally use Hilt here, but for Widgets we use a direct entry point or WorkManager.
        // For simplicity, we'll use a WorkManager task that handles the DB insert.
        val workRequest = androidx.work.OneTimeWorkRequestBuilder<com.location.vitalflow.data.worker.WaterWidgetWorker>()
            .setInputData(androidx.work.workDataOf("AMOUNT" to amount))
            .build()
        androidx.work.WorkManager.getInstance(context).enqueue(workRequest)
        
        // Update widget state if needed
        updateAppWidgetState(context, glanceId) { prefs ->
            // Update local glance state if we were tracking it there
        }
        WaterWidget().update(context, glanceId)
    }
}
