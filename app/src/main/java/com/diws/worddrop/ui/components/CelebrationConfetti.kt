package com.diws.worddrop.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.random.Random

private data class ConfettiParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var rotation: Float,
    var rotationSpeed: Float,
    val size: Float,
    val color: Color,
    val isDropShape: Boolean
)

private val CONFETTI_COLORS = listOf(
    Color(0xFF8C80FF), // Purple
    Color(0xFF4DDCC6), // Teal
    Color(0xFFFFD54F), // Gold
    Color(0xFFFF6B8B), // Pink
    Color(0xFF80D8FF), // Cyan
    Color(0xFFFFB74D)  // Amber
)

@Composable
fun CelebrationConfetti(
    modifier: Modifier = Modifier,
    particleCount: Int = 50
) {
    val particles = remember { mutableStateListOf<ConfettiParticle>() }
    val alphaAnim = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // Initialize particles at top of screen with varied velocities
        particles.clear()
        for (i in 0 until particleCount) {
            particles.add(
                ConfettiParticle(
                    x = Random.nextFloat(),
                    y = Random.nextFloat() * -0.3f, // Start above viewport
                    vx = (Random.nextFloat() - 0.5f) * 0.008f,
                    vy = 0.008f + Random.nextFloat() * 0.014f,
                    rotation = Random.nextFloat() * 360f,
                    rotationSpeed = (Random.nextFloat() - 0.5f) * 12f,
                    size = 14f + Random.nextFloat() * 16f,
                    color = CONFETTI_COLORS.random(),
                    isDropShape = Random.nextBoolean()
                )
            )
        }

        // Animate physics for 3 seconds
        val startTime = System.currentTimeMillis()
        while (isActive && System.currentTimeMillis() - startTime < 3800) {
            for (p in particles) {
                p.x += p.vx
                p.y += p.vy
                p.vy += 0.0003f // Subtle gravity
                p.rotation += p.rotationSpeed
            }
            delay(16) // ~60fps
        }

        // Fade out
        alphaAnim.animateTo(0f, animationSpec = tween(600))
    }

    if (alphaAnim.value > 0f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val currentAlpha = alphaAnim.value

            particles.forEach { p ->
                val px = p.x * canvasWidth
                val py = p.y * canvasHeight

                if (py in -50f..(canvasHeight + 50f)) {
                    rotate(p.rotation, pivot = Offset(px, py)) {
                        if (p.isDropShape) {
                            // Raindrop / teardrop shape for "Wordzip"
                            drawCircle(
                                color = p.color.copy(alpha = currentAlpha),
                                radius = p.size / 2f,
                                center = Offset(px, py)
                            )
                        } else {
                            // Confetti ribbon strip
                            drawRect(
                                color = p.color.copy(alpha = currentAlpha),
                                topLeft = Offset(px - p.size / 2f, py - p.size / 4f),
                                size = Size(p.size, p.size / 2f)
                            )
                        }
                    }
                }
            }
        }
    }
}
