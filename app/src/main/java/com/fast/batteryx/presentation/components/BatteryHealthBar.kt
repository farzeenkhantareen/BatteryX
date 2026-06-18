package com.fast.batteryx.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fast.batteryx.ui.theme.GradientTokens
import com.fast.batteryx.ui.theme.healthColor

@Composable
fun BatteryHealthBar(
    healthPercent: Float,
    modifier: Modifier = Modifier
) {
    var animateTrigger by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (animateTrigger) healthPercent / 100f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "healthBarProgress"
    )

    LaunchedEffect(healthPercent) {
        animateTrigger = true
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
        ) {
            Text(
                text = "Remaining Capacity",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${healthPercent.toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = healthColor(healthPercent)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(
                        brush = GradientTokens.healthBrush(healthPercent)
                    )
            )
        }
    }
}
