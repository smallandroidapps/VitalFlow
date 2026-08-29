package com.location.vitalflow.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import kotlin.math.sin

@Composable
fun WaterWaveCanvas(
    modifier: Modifier = Modifier,
    fillLevel: Float, // 0.0f to 1.0f
    waveColor: Color = Color(0xFF2196F3),
    bottleColor: Color = Color.Gray.copy(alpha = 0.3f)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Draw Bottle Outline (Stylized)
        val bottlePath = Path().apply {
            moveTo(width * 0.25f, height * 0.1f)
            lineTo(width * 0.75f, height * 0.1f)
            lineTo(width * 0.8f, height * 0.2f)
            lineTo(width * 0.8f, height * 0.85f)
            quadraticTo(width * 0.8f, height * 0.95f, width * 0.7f, height * 0.95f)
            lineTo(width * 0.3f, height * 0.95f)
            quadraticTo(width * 0.2f, height * 0.95f, width * 0.2f, height * 0.85f)
            lineTo(width * 0.2f, height * 0.2f)
            close()
        }
        drawPath(bottlePath, bottleColor)

        // Clip the wave inside the bottle
        clipPath(bottlePath) {
            val wavePath = Path()
            val fillHeight = height * (1f - fillLevel)
            val waveAmplitude = 10.dp.toPx()
            val waveFrequency = 0.015f

            wavePath.moveTo(0f, height)
            wavePath.lineTo(0f, fillHeight)

            // Step by 5 pixels for better performance
            var x = 0f
            while (x <= width) {
                val y = fillHeight + waveAmplitude * sin(x * waveFrequency + waveOffset)
                wavePath.lineTo(x, y)
                x += 5f
            }

            wavePath.lineTo(width, height)
            wavePath.close()

            drawPath(wavePath, waveColor)
        }
    }
}
