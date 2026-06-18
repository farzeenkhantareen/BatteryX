package com.fast.batteryx.presentation.components

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.fast.batteryx.ui.theme.ElectricBlue
import com.fast.batteryx.ui.theme.NeonGreen
import kotlin.random.Random

private data class Particle(
    val xPercent: Float,
    val initialY: Float,
    val speed: Float,
    val radius: Float,
    val color: Color,
    val maxAlpha: Float
)

@Composable
fun ChargeParticles(
    modifier: Modifier = Modifier,
    particleCount: Int = 15,
    isCharging: Boolean = true
) {
    if (!isCharging) return

    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val animationState by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleFloat"
    )

    val particles = remember(particleCount) {
        List(particleCount) {
            Particle(
                xPercent = Random.nextFloat(),
                initialY = Random.nextFloat(),
                speed = 0.3f + Random.nextFloat() * 0.7f,
                radius = 3f + Random.nextFloat() * 8f,
                color = if (Random.nextBoolean()) ElectricBlue else NeonGreen,
                maxAlpha = 0.2f + Random.nextFloat() * 0.5f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        particles.forEach { particle ->
            // Calculate current y coordinate
            val progress = (animationState * particle.speed + particle.initialY) % 1.0f
            val y = height * (1.0f - progress) // move upwards
            val x = width * particle.xPercent

            // Fade out near the top
            val alpha = if (progress > 0.8f) {
                particle.maxAlpha * (1.0f - progress) / 0.2f
            } else if (progress < 0.2f) {
                particle.maxAlpha * (progress / 0.2f)
            } else {
                particle.maxAlpha
            }

            drawCircle(
                color = particle.color.copy(alpha = alpha.coerceIn(0f, 1f)),
                radius = particle.radius,
                center = Offset(x, y)
            )
        }
    }
}
