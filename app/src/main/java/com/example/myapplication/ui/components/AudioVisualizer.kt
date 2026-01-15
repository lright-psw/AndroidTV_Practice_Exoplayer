package com.example.myapplication.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun AudioVisualizer(isPlaying: Boolean) {
    val barCount = 40
    val amplitudes = remember { mutableStateListOf<Float>().apply { repeat(barCount) { add(0.1f) } } }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                for (i in 0 until barCount) {
                    // ★ 에러 발생 지점 수정: Random.nextFloat()을 쓰거나 범위를 정확히 지정
                    amplitudes[i] = Random.nextFloat() * 0.8f + 0.2f
                }
                delay(150)
            }
        } else {
            for (i in 0 until barCount) amplitudes[i] = 0.1f
        }
    }

    Canvas(modifier = Modifier.fillMaxSize().padding(bottom = 40.dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val barWidth = canvasWidth / (barCount * 1.5f)
        val space = barWidth * 0.5f

        for (i in 0 until barCount) {
            // 부드러운 움직임을 위해 높이 값을 보정
            val barHeight = amplitudes[i] * (canvasHeight * 0.4f)
            val xOffset = i * (barWidth + space) + (barWidth / 2)

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6C7CFF).copy(alpha = 0.6f),
                        Color(0xFF6C7CFF).copy(alpha = 0.0f)
                    )
                ),
                topLeft = Offset(xOffset, canvasHeight - barHeight),
                size = Size(barWidth, barHeight)
            )
        }
    }
}