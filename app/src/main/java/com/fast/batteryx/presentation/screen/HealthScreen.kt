package com.fast.batteryx.presentation.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fast.batteryx.data.entity.HealthHistory
import com.fast.batteryx.presentation.components.BatteryHealthBar
import com.fast.batteryx.presentation.components.GlassCard
import com.fast.batteryx.presentation.components.SectionHeader
import com.fast.batteryx.presentation.viewmodel.HealthViewModel
import com.fast.batteryx.ui.theme.ElectricBlue
import com.fast.batteryx.ui.theme.GradientElectric
import com.fast.batteryx.ui.theme.GradientHealth
import com.fast.batteryx.ui.theme.healthColor

@Composable
fun HealthScreen(
    viewModel: HealthViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Radial Health Gauge
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentAlignment = Alignment.Center
            ) {
                RadialHealthGauge(
                    healthPercent = uiState.healthPercent,
                    label = uiState.healthLabel,
                    modifier = Modifier.size(200.dp)
                )
            }
        }

        // Capacity Details Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Capacity Analysis",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Design Capacity",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (uiState.designCapacityMah > 0) "${uiState.designCapacityMah} mAh" else "Not reported",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Estimated Capacity",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (uiState.estimatedCapacityMah > 0) "${uiState.estimatedCapacityMah} mAh" else "Calculating...",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ElectricBlue
                            )
                        }
                    }

                    BatteryHealthBar(healthPercent = uiState.healthPercent)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Capacity Lost",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${uiState.capacityLostMah} mAh (${uiState.wearPercent.toInt()}% Wear)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        // Degradation Trend Chart
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionHeader(title = "Health History Trend")
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (uiState.healthHistory.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No history recorded yet. Snapshots are recorded daily.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            HealthHistoryChart(
                                history = uiState.healthHistory,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            )
                        }
                    }
                }
            }
        }

        // Future Predictions Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionHeader(title = "Lifetime Forecast")
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        items(uiState.predictions) { prediction ->
            PredictionCard(prediction = prediction)
        }

        // Padding at the bottom for navigation bar space
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun RadialHealthGauge(
    healthPercent: Float,
    label: String,
    modifier: Modifier = Modifier
) {
    var animateTrigger by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (animateTrigger) healthPercent / 100f else 0f,
        animationSpec = tween(durationMillis = 1200),
        label = "radialGaugeProgress"
    )

    LaunchedEffect(healthPercent) {
        animateTrigger = true
    }

    val healthColor = healthColor(healthPercent)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val innerSize = size.width - strokeWidth
            val offset = strokeWidth / 2

            // Background arc
            drawArc(
                color = Color.White.copy(alpha = 0.08f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = Offset(offset, offset),
                size = Size(innerSize, innerSize),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress arc
            drawArc(
                brush = Brush.sweepGradient(
                    0f to healthColor.copy(alpha = 0.5f),
                    0.5f to healthColor,
                    1f to healthColor
                ),
                startAngle = 135f,
                sweepAngle = 270f * progress,
                useCenter = false,
                topLeft = Offset(offset, offset),
                size = Size(innerSize, innerSize),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = healthColor
            )
        }
    }
}

@Composable
fun PredictionCard(
    prediction: com.fast.batteryx.presentation.viewmodel.PredictionItem,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Forecast: ${prediction.label}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Confidence: ${(prediction.confidence * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${prediction.predictedHealth.toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = healthColor(prediction.predictedHealth)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { prediction.predictedHealth / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = healthColor(prediction.predictedHealth),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                )
            }
        }
    }
}

@Composable
fun HealthHistoryChart(
    history: List<HealthHistory>,
    modifier: Modifier = Modifier
) {
    val reversed = remember(history) { history.sortedBy { it.date } }
    val maxPercentage = 100f
    val minPercentage = remember(reversed) { 
        (reversed.minOfOrNull { it.healthPercentage } ?: 80f) - 5f
    }.coerceAtLeast(0f)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val padding = 20.dp.toPx()

        val chartWidth = width - padding * 2
        val chartHeight = height - padding * 2

        if (reversed.size < 2) return@Canvas

        val path = Path()
        val fillPath = Path()

        val points = reversed.mapIndexed { index, item ->
            val x = padding + (index.toFloat() / (reversed.size - 1)) * chartWidth
            val range = maxPercentage - minPercentage
            val percentageOffset = (item.healthPercentage - minPercentage) / if (range == 0f) 1f else range
            val y = padding + (1f - percentageOffset) * chartHeight
            Offset(x, y)
        }

        path.moveTo(points.first().x, points.first().y)
        fillPath.moveTo(points.first().x, points.first().y)

        for (i in 1 until points.size) {
            path.lineTo(points[i].x, points[i].y)
            fillPath.lineTo(points[i].x, points[i].y)
        }

        fillPath.lineTo(points.last().x, height - padding)
        fillPath.lineTo(points.first().x, height - padding)
        fillPath.close()

        // Draw background gradient fill
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    ElectricBlue.copy(alpha = 0.2f),
                    Color.Transparent
                )
            )
        )

        // Draw main line
        drawPath(
            path = path,
            color = ElectricBlue,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        // Draw data points
        points.forEach { point ->
            drawCircle(
                color = Color.White,
                radius = 4.dp.toPx(),
                center = point
            )
            drawCircle(
                color = ElectricBlue,
                radius = 2.dp.toPx(),
                center = point
            )
        }
    }
}
