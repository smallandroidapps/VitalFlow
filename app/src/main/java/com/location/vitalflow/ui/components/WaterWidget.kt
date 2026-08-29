package com.location.vitalflow.ui.components

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.*
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

class WaterWidget : GlanceAppWidget() {
    
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val totalMl = prefs[intPreferencesKey("TOTAL_ML")] ?: 0
            
            GlanceTheme {
                WidgetContent(totalMl)
            }
        }
    }

    @androidx.compose.runtime.Composable
    private fun WidgetContent(totalMl: Int) {
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
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = "${totalMl}ml Today",
                style = TextStyle(color = GlanceTheme.colors.primary, fontWeight = FontWeight.Medium)
            )
            Spacer(modifier = GlanceModifier.height(8.dp))
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
        
        val workRequest = androidx.work.OneTimeWorkRequestBuilder<com.location.vitalflow.data.worker.WaterWidgetWorker>()
            .setInputData(androidx.work.workDataOf("AMOUNT" to amount))
            .build()
        androidx.work.WorkManager.getInstance(context).enqueue(workRequest)
        
        // Update local glance state for immediate feedback
        updateAppWidgetState(context, glanceId) { prefs ->
            val key = intPreferencesKey("TOTAL_ML")
            val current = prefs[key] ?: 0
            prefs.toMutablePreferences().apply {
                this[key] = current + amount
            }
        }
        WaterWidget().update(context, glanceId)
    }
}
