package com.location.vitalflow.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import kotlin.math.sin

@Composable
fun WaterWaveCanvas(
    modifier: Modifier = Modifier,
    fillLevel: Float, // 0.0f to 1.0f
    waveColor: Color = Color(0xFF4FC3F7),
    glassColor: Color = Color.White.copy(alpha = 0.2f)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Define a modern tapered glass path
        val glassPath = Path().apply {
            moveTo(width * 0.15f, height * 0.05f) // Top left
            lineTo(width * 0.85f, height * 0.05f) // Top right
            lineTo(width * 0.78f, height * 0.9f)  // Bottom right (tapered)
            quadraticTo(width * 0.76f, height * 0.95f, width * 0.7f, height * 0.95f) // Bottom right corner
            lineTo(width * 0.3f, height * 0.95f)  // Bottom flat
            quadraticTo(width * 0.24f, height * 0.95f, width * 0.22f, height * 0.9f) // Bottom left corner
            lineTo(width * 0.15f, height * 0.05f) // Back to top left
            close()
        }

        // 1. Draw the Glass Background (inner tint)
        drawPath(glassPath, glassColor)

        // 2. Draw the Water with Waves
        clipPath(glassPath) {
            val wavePath = Path()
            val fillHeight = height * (1f - fillLevel)
            val waveAmplitude = 8.dp.toPx()
            val waveFrequency = 0.012f

            wavePath.moveTo(-width, height)
            wavePath.lineTo(-width, fillHeight)

            var x = 0f
            val totalWidth = width
            while (x <= totalWidth) {
                val y = fillHeight + waveAmplitude * sin(x * waveFrequency + waveOffset)
                wavePath.lineTo(x, y)
                x += 4f
            }

            wavePath.lineTo(width, height)
            wavePath.close()

            // Dynamic blue gradient for water
            val waterBrush = Brush.verticalGradient(
                colors = listOf(waveColor.copy(alpha = 0.8f), waveColor),
                startY = fillHeight,
                endY = height
            )
            drawPath(wavePath, waterBrush)
            
            // Add a lighter wave highlight
            val highlightPath = Path()
            x = 0f
            highlightPath.moveTo(0f, fillHeight + 10f)
            while (x <= totalWidth) {
                val y = (fillHeight + 15f) + waveAmplitude * sin(x * waveFrequency + waveOffset + 0.5f)
                highlightPath.lineTo(x, y)
                x += 4f
            }
            drawPath(highlightPath, Color.White.copy(alpha = 0.2f), style = Stroke(width = 2.dp.toPx()))
        }

        // 3. Draw Glass Rim and Highlights (Outer edge)
        drawPath(
            path = glassPath,
            color = Color.White.copy(alpha = 0.5f),
            style = Stroke(width = 3.dp.toPx())
        )

        // Side reflection highlight
        val reflectionPath = Path().apply {
            moveTo(width * 0.22f, height * 0.15f)
            lineTo(width * 0.25f, height * 0.8f)
        }
        drawPath(reflectionPath, Color.White.copy(alpha = 0.3f), style = Stroke(width = 4.dp.toPx()))
    }
}
