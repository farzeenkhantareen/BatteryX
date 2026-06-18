package com.fast.batteryx.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fast.batteryx.ui.theme.DarkBorder
import com.fast.batteryx.ui.theme.ElectricBlue
import kotlin.math.cos
import kotlin.math.sin

/**
 * Premium animated circular battery gauge.
 * Used for health display, battery level hero, and various metric displays.
 */
@Composable
fun BatteryGauge(
    value: Float,          // 0f-100f
    maxValue: Float = 100f,
    label: String = "",
    sublabel: String = "",
    valueText: String? = null,
    primaryColor: Color = ElectricBlue,
    trackColor: Color = DarkBorder,
    strokeWidth: Dp = 12.dp,
    size: Dp = 180.dp,
    modifier: Modifier = Modifier
) {
    val animatedValue by animateFloatAsState(
        targetValue = (value / maxValue).coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "gaugeAnim"
    )

    // Subtle glow pulse
    val infiniteTransition = rememberInfiniteTransition(label = "gaugePulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val sweepAngle = animatedValue * 270f

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val canvasSize = this.size
            val stroke = strokeWidth.toPx()
            val arcSize = Size(canvasSize.width - stroke, canvasSize.height - stroke)
            val topLeft = Offset(stroke / 2, stroke / 2)
            val startAngle = 135f

            // Track
            drawArc(
                color = trackColor,
                startAngle = startAngle,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Value arc with gradient
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.6f),
                        primaryColor,
                        primaryColor.copy(alpha = 0.8f)
                    )
                ),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Glow dot at end of arc
            if (sweepAngle > 5f) {
                val endAngleRad = Math.toRadians((startAngle + sweepAngle).toDouble())
                val centerX = canvasSize.width / 2
                val centerY = canvasSize.height / 2
                val radius = (canvasSize.width - stroke) / 2

                val dotX = centerX + radius * cos(endAngleRad).toFloat()
                val dotY = centerY + radius * sin(endAngleRad).toFloat()

                drawCircle(
                    color = primaryColor.copy(alpha = glowAlpha),
                    radius = stroke * 1.5f,
                    center = Offset(dotX, dotY)
                )
                drawCircle(
                    color = primaryColor,
                    radius = stroke * 0.4f,
                    center = Offset(dotX, dotY)
                )
            }
        }

        // Center text
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valueText ?: "${value.toInt()}%",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = if (size > 150.dp) 42.sp else 28.sp
                ),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (label.isNotEmpty()) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    color = primaryColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (sublabel.isNotEmpty()) {
                Text(
                    text = sublabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
