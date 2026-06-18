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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.geometry.CornerRadius
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
import com.fast.batteryx.data.entity.TemperatureHistory
import com.fast.batteryx.presentation.components.GlassCard
import com.fast.batteryx.presentation.components.SectionHeader
import com.fast.batteryx.presentation.viewmodel.TempPeriod
import com.fast.batteryx.presentation.viewmodel.TemperatureViewModel
import com.fast.batteryx.ui.theme.ElectricBlue
import com.fast.batteryx.ui.theme.temperatureColor

@Composable
fun TemperatureScreen(
    viewModel: TemperatureViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val periods = TempPeriod.values()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Temperature Gauge & Large Metric
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ThermometerWidget(
                        tempCelsius = uiState.currentTemp,
                        modifier = Modifier.size(width = 50.dp, height = 150.dp)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Current Temperature",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${uiState.currentTemp.toInt()}°C",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = temperatureColor(uiState.currentTemp)
                        )
                        val advice = when {
                            uiState.currentTemp >= 45f -> "⚠️ Dangerously hot! Stop charging."
                            uiState.currentTemp >= 38f -> "🌡️ Getting warm. Let it cool down."
                            else -> "✅ Safe operating temperature."
                        }
                        Text(
                            text = advice,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = temperatureColor(uiState.currentTemp)
                        )
                    }
                }
            }
        }

        // Stats Row (Average, Peak)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassCard(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Average Temperature",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${uiState.averageTemp.toInt()}°C",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = temperatureColor(uiState.averageTemp)
                        )
                    }
                }

                GlassCard(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Peak Temperature",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${uiState.peakTemp.toInt()}°C",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = temperatureColor(uiState.peakTemp)
                        )
                    }
                }
            }
        }

        // Period Selection Tabs
        item {
            TabRow(
                selectedTabIndex = uiState.selectedPeriod.ordinal,
                containerColor = Color.Transparent,
                contentColor = ElectricBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[uiState.selectedPeriod.ordinal]),
                        color = ElectricBlue
                    )
                },
                divider = {}
            ) {
                periods.forEach { period ->
                    Tab(
                        selected = uiState.selectedPeriod == period,
                        onClick = { viewModel.selectPeriod(period) },
                        text = {
                            Text(
                                text = period.label,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                }
            }
        }

        // History Chart
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionHeader(title = "Temperature Logs")
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (uiState.history.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No history recorded for this period.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            TemperatureHistoryChart(
                                history = uiState.history,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ThermometerWidget(
    tempCelsius: Float,
    modifier: Modifier = Modifier
) {
    var animateTrigger by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (animateTrigger) (tempCelsius / 60f).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "thermometerProgress"
    )

    LaunchedEffect(tempCelsius) {
        animateTrigger = true
    }

    val activeColor = temperatureColor(tempCelsius)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val tubeWidth = 10.dp.toPx()
        val bulbRadius = 14.dp.toPx()
        val bulbX = width / 2
        val bulbY = height - bulbRadius

        // 1. Draw background tube
        drawRoundRect(
            color = Color.White.copy(alpha = 0.1f),
            topLeft = Offset(width / 2 - tubeWidth / 2, 0f),
            size = Size(tubeWidth, height - bulbRadius),
            cornerRadius = CornerRadius(tubeWidth / 2, tubeWidth / 2)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.1f),
            radius = bulbRadius,
            center = Offset(bulbX, bulbY)
        )

        // 2. Draw active fluid level
        val activeHeight = (height - bulbRadius * 2) * progress
        val fluidY = height - bulbRadius * 2 - activeHeight

        drawRoundRect(
            color = activeColor,
            topLeft = Offset(width / 2 - tubeWidth / 2, fluidY),
            size = Size(tubeWidth, activeHeight + bulbRadius),
            cornerRadius = CornerRadius(tubeWidth / 2, tubeWidth / 2)
        )
        drawCircle(
            color = activeColor,
            radius = bulbRadius,
            center = Offset(bulbX, bulbY)
        )
    }
}

@Composable
fun TemperatureHistoryChart(
    history: List<TemperatureHistory>,
    modifier: Modifier = Modifier
) {
    val sorted = remember(history) { history.sortedBy { it.timestamp } }
    val maxTemp = remember(sorted) { (sorted.maxOfOrNull { it.temperature } ?: 40).coerceAtLeast(35) }
    val minTemp = remember(sorted) { (sorted.minOfOrNull { it.temperature } ?: 20).coerceAtMost(25) }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val padding = 12.dp.toPx()

        val chartWidth = width - padding * 2
        val chartHeight = height - padding * 2

        if (sorted.size < 2) return@Canvas

        val path = Path()
        val fillPath = Path()

        val points = sorted.mapIndexed { index, item ->
            val x = padding + (index.toFloat() / (sorted.size - 1)) * chartWidth
            val range = maxTemp - minTemp
            val tempOffset = (item.temperature - minTemp).toFloat() / if (range == 0) 1f else range.toFloat()
            val y = padding + (1f - tempOffset.coerceIn(0f, 1f)) * chartHeight
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

        val firstTemp = sorted.first().temperature.toFloat()
        val endTemp = sorted.last().temperature.toFloat()

        // Area fill under curve
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    temperatureColor(endTemp).copy(alpha = 0.25f),
                    Color.Transparent
                )
            )
        )

        // Curve line
        drawPath(
            path = path,
            color = temperatureColor(endTemp),
            style = Stroke(
                width = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}
