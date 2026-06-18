package com.fast.batteryx.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fast.batteryx.data.entity.ChargingSession
import com.fast.batteryx.presentation.components.GlassCard
import com.fast.batteryx.presentation.components.SectionHeader
import com.fast.batteryx.presentation.viewmodel.ChargingReading
import com.fast.batteryx.presentation.viewmodel.ChargingViewModel
import com.fast.batteryx.ui.theme.ElectricBlue
import com.fast.batteryx.ui.theme.GradientElectric
import com.fast.batteryx.ui.theme.NeonGreen
import com.fast.batteryx.ui.theme.glassCard
import java.time.format.DateTimeFormatter
import kotlin.math.abs

@Composable
fun ChargingScreen(
    viewModel: ChargingViewModel,
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
        // Active/Inactive Monitor Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (uiState.isCharging) "Charging Active" else "Discharging",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.isCharging) NeonGreen else MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (uiState.isCharging) NeonGreen.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = uiState.chargerType,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.isCharging) NeonGreen else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ChargingMetric(
                            label = "Power",
                            value = String.format("%.1f W", uiState.powerWatts),
                            color = NeonGreen
                        )
                        ChargingMetric(
                            label = "Current",
                            value = "${uiState.currentMa} mA",
                            color = if (uiState.currentMa >= 0) NeonGreen else MaterialTheme.colorScheme.error
                        )
                        ChargingMetric(
                            label = "Voltage",
                            value = String.format("%.2f V", uiState.voltage),
                            color = ElectricBlue
                        )
                    }
                }
            }
        }

        // Live Charging Graph Card
        if (uiState.isCharging) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionHeader(title = "Live Current Monitor")
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            LiveChargingGraph(
                                readings = uiState.chargingReadings,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                            )
                        }
                    }
                }
            }
        }

        // Charge Alarm Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Charge Alert Alarm",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Notify at target limit to reduce battery wear",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.chargeAlarmEnabled,
                            onCheckedChange = { viewModel.toggleChargeAlarm(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ElectricBlue,
                                checkedTrackColor = ElectricBlue.copy(alpha = 0.4f)
                            )
                        )
                    }

                    AnimatedVisibility(visible = uiState.chargeAlarmEnabled) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { 
                                        if (uiState.chargeAlarmPercent > 70) 
                                            viewModel.setChargeAlarmPercent(uiState.chargeAlarmPercent - 5)
                                    },
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                                }

                                Text(
                                    text = "${uiState.chargeAlarmPercent}%",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ElectricBlue
                                )

                                IconButton(
                                    onClick = { 
                                        if (uiState.chargeAlarmPercent < 95) 
                                            viewModel.setChargeAlarmPercent(uiState.chargeAlarmPercent + 5)
                                    },
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase")
                                }
                            }

                            Slider(
                                value = uiState.chargeAlarmPercent.toFloat(),
                                onValueChange = { viewModel.setChargeAlarmPercent(it.toInt()) },
                                valueRange = 70f..95f,
                                steps = 4,
                                colors = SliderDefaults.colors(
                                    thumbColor = ElectricBlue,
                                    activeTrackColor = ElectricBlue,
                                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }

        // Charge Session History
        item {
            SectionHeader(title = "Charging Session History")
        }

        if (uiState.recentSessions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No charging sessions recorded yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(uiState.recentSessions) { session ->
                ChargingSessionItem(session = session)
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ChargingMetric(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun LiveChargingGraph(
    readings: List<ChargingReading>,
    modifier: Modifier = Modifier
) {
    if (readings.isEmpty()) return

    val maxVal = remember(readings) { (readings.maxOfOrNull { abs(it.currentMa) } ?: 1000).coerceAtLeast(100) }
    val minVal = 0

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val padding = 10.dp.toPx()

        val graphWidth = width - padding * 2
        val graphHeight = height - padding * 2

        val path = Path()
        val fillPath = Path()

        val maxPoints = 60
        val points = readings.mapIndexed { index, reading ->
            val x = padding + (index.toFloat() / maxPoints) * graphWidth
            val yOffset = abs(reading.currentMa).toFloat() / maxVal
            val y = padding + (1f - yOffset.coerceIn(0f, 1f)) * graphHeight
            Offset(x, y)
        }

        if (points.isNotEmpty()) {
            path.moveTo(points.first().x, points.first().y)
            fillPath.moveTo(points.first().x, points.first().y)

            for (i in 1 until points.size) {
                path.lineTo(points[i].x, points[i].y)
                fillPath.lineTo(points[i].x, points[i].y)
            }

            fillPath.lineTo(points.last().x, height - padding)
            fillPath.lineTo(points.first().x, height - padding)
            fillPath.close()

            // Fill background gradient
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(NeonGreen.copy(alpha = 0.2f), Color.Transparent)
                )
            )

            // Draw line
            drawPath(
                path = path,
                color = NeonGreen,
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }
    }
}

@Composable
fun ChargingSessionItem(
    session: ChargingSession,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val dateStr = session.startTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy · HH:mm"))
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val startPct = session.startPercentage
                    val endPct = session.endPercentage ?: startPct
                    val diff = endPct - startPct
                    val chargeText = if (session.endPercentage != null) {
                        "Charge: $startPct% → $endPct% (+$diff%)"
                    } else {
                        "Charge: $startPct% (Charging...)"
                    }
                    Text(
                        text = chargeText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "+${session.capacityAdded.toInt()} mAh",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NeonGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Average Current",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${session.averageCurrent.toInt()} mA",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Charger Type",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        val typeStr = when (session.chargerType) {
                            1 -> "USB Port"
                            2 -> "AC Wall Charger"
                            4 -> "Wireless Charger"
                            else -> "Wall Fast Charger"
                        }
                        Text(
                            text = typeStr,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
