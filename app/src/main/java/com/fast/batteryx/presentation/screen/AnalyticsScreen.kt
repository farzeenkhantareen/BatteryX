package com.fast.batteryx.presentation.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.fast.batteryx.data.entity.BatterySample
import com.fast.batteryx.presentation.components.GlassCard
import com.fast.batteryx.presentation.components.SectionHeader
import com.fast.batteryx.presentation.viewmodel.AnalyticsPeriod
import com.fast.batteryx.presentation.viewmodel.AnalyticsViewModel
import com.fast.batteryx.ui.theme.ChartPrimary
import com.fast.batteryx.ui.theme.ChartSecondary
import com.fast.batteryx.ui.theme.ChartTertiary
import com.fast.batteryx.ui.theme.ElectricBlue

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val periods = AnalyticsPeriod.values()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Period Tab Selection
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

        // Consumption Trend Line Chart Card
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionHeader(title = "Battery Level History")
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (uiState.samples.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No metrics collected during this period.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            BatteryLevelChart(
                                samples = uiState.samples,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            )
                        }
                    }
                }
            }
        }

        // Summary Stats Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Average Current Drain",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = String.format("%.0f mA", uiState.avgDrainPerHour),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Collection Data Points",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${uiState.samples.size}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = ElectricBlue
                        )
                    }
                }
            }
        }

        // Usage breakdown (Canvas Donut Chart)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionHeader(title = "Screen vs. Standby Breakdown")
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        UsageDonutChart(
                            screenPercent = uiState.screenUsagePercent,
                            standbyPercent = uiState.standbyUsagePercent,
                            chargingPercent = uiState.chargingUsagePercent,
                            modifier = Modifier.size(130.dp)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            LegendItem(label = "Active (Screen-On)", percentage = uiState.screenUsagePercent, color = ChartPrimary)
                            LegendItem(label = "Standby (Idle)", percentage = uiState.standbyUsagePercent, color = ChartSecondary)
                            LegendItem(label = "Charging time", percentage = uiState.chargingUsagePercent, color = ChartTertiary)
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
fun LegendItem(
    label: String,
    percentage: Float,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = "$label: ${percentage.toInt()}%",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun BatteryLevelChart(
    samples: List<BatterySample>,
    modifier: Modifier = Modifier
) {
    val sortedSamples = remember(samples) { samples.sortedBy { it.timestamp } }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val padding = 15.dp.toPx()

        val chartWidth = width - padding * 2
        val chartHeight = height - padding * 2

        if (sortedSamples.size < 2) return@Canvas

        val path = Path()
        val fillPath = Path()

        val points = sortedSamples.mapIndexed { index, sample ->
            val x = padding + (index.toFloat() / (sortedSamples.size - 1)) * chartWidth
            val y = padding + (1f - (sample.percentage / 100f)) * chartHeight
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

        // Draw area fill
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    ElectricBlue.copy(alpha = 0.25f),
                    Color.Transparent
                )
            )
        )

        // Draw line
        drawPath(
            path = path,
            color = ElectricBlue,
            style = Stroke(
                width = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}

@Composable
fun UsageDonutChart(
    screenPercent: Float,
    standbyPercent: Float,
    chargingPercent: Float,
    modifier: Modifier = Modifier
) {
    var animateTrigger by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (animateTrigger) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "donutProgress"
    )

    LaunchedEffect(screenPercent) {
        animateTrigger = true
    }

    Canvas(modifier = modifier) {
        val strokeWidth = 14.dp.toPx()
        val donutSize = size.width - strokeWidth
        val offset = strokeWidth / 2

        val total = screenPercent + standbyPercent + chargingPercent
        if (total <= 0f) return@Canvas

        val screenAngle = (screenPercent / total) * 360f * progress
        val standbyAngle = (standbyPercent / total) * 360f * progress
        val chargingAngle = (chargingPercent / total) * 360f * progress

        // Draw Screen On arc
        drawArc(
            color = ChartPrimary,
            startAngle = -90f,
            sweepAngle = screenAngle,
            useCenter = false,
            topLeft = Offset(offset, offset),
            size = Size(donutSize, donutSize),
            style = Stroke(width = strokeWidth)
        )

        // Draw Standby arc
        drawArc(
            color = ChartSecondary,
            startAngle = -90f + screenAngle,
            sweepAngle = standbyAngle,
            useCenter = false,
            topLeft = Offset(offset, offset),
            size = Size(donutSize, donutSize),
            style = Stroke(width = strokeWidth)
        )

        // Draw Charging arc
        drawArc(
            color = ChartTertiary,
            startAngle = -90f + screenAngle + standbyAngle,
            sweepAngle = chargingAngle,
            useCenter = false,
            topLeft = Offset(offset, offset),
            size = Size(donutSize, donutSize),
            style = Stroke(width = strokeWidth)
        )
    }
}
