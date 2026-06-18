package com.fast.batteryx.presentation.screen

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fast.batteryx.presentation.components.BatteryGauge
import com.fast.batteryx.presentation.components.ChargeParticles
import com.fast.batteryx.presentation.components.GlassCard
import com.fast.batteryx.presentation.components.MetricChip
import com.fast.batteryx.presentation.components.SectionHeader
import com.fast.batteryx.presentation.viewmodel.DashboardViewModel
import com.fast.batteryx.ui.theme.ElectricBlue
import com.fast.batteryx.ui.theme.GradientElectric
import com.fast.batteryx.ui.theme.NeonGreen
import com.fast.batteryx.ui.theme.batteryColor
import com.fast.batteryx.ui.theme.glassCard
import com.fast.batteryx.ui.theme.healthColor
import com.fast.batteryx.ui.theme.temperatureColor

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Battery Gauge Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background charging particles if active
                ChargeParticles(isCharging = uiState.isCharging)

                BatteryGauge(
                    value = uiState.batteryPercent.toFloat(),
                    label = if (uiState.isCharging) "Charging" else "Discharging",
                    sublabel = "${uiState.currentMa} mA",
                    primaryColor = if (uiState.isCharging) NeonGreen else batteryColor(uiState.batteryPercent),
                    modifier = Modifier.size(240.dp)
                )
            }
        }

        // Horizontal metrics chip list
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    MetricChip(
                        label = "Temperature",
                        value = "${uiState.temperature.toInt()}°C",
                        icon = Icons.Default.Thermostat,
                        tint = temperatureColor(uiState.temperature)
                    )
                }
                item {
                    MetricChip(
                        label = "Voltage",
                        value = String.format("%.2f V", uiState.voltage),
                        icon = Icons.Default.FlashOn,
                        tint = ElectricBlue
                    )
                }
                item {
                    MetricChip(
                        label = "Current",
                        value = "${uiState.currentMa} mA",
                        icon = Icons.Default.Speed,
                        tint = if (uiState.currentMa >= 0) NeonGreen else MaterialTheme.colorScheme.error
                    )
                }
                item {
                    MetricChip(
                        label = "Health",
                        value = "${uiState.healthPercent.toInt()}%",
                        icon = Icons.Default.Info,
                        tint = healthColor(uiState.healthPercent)
                    )
                }
            }
        }

        // Quick Stats Section (2x2 Grid using columns/rows)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeader(title = "Stats Overview")
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card 1: Screen Time
                    StatCard(
                        title = "Est. Active Use",
                        value = uiState.screenTimeRemaining,
                        sub = "remaining time",
                        icon = Icons.Default.Timelapse,
                        color = ElectricBlue,
                        modifier = Modifier.weight(1f)
                    )

                    // Card 2: Charging Speed
                    StatCard(
                        title = "Charge Speed",
                        value = uiState.chargingSpeed,
                        sub = if (uiState.isCharging) "active rating" else "not charging",
                        icon = Icons.Default.Speed,
                        color = NeonGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card 3: Battery Wear
                    StatCard(
                        title = "Wear Level",
                        value = "${uiState.wearPercent.toInt()}%",
                        sub = "capacity loss",
                        icon = Icons.Default.Warning,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )

                    // Card 4: Cycle Count
                    StatCard(
                        title = "Charge Cycles",
                        value = "${uiState.cycleCount}",
                        sub = "total completed",
                        icon = Icons.Default.Power,
                        color = CyberPurple,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Smart Insights Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                SectionHeader(title = "Smart Insights")
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        items(uiState.insights) { insight ->
            InsightCard(insight = insight)
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = sub,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun InsightCard(
    insight: com.fast.batteryx.presentation.viewmodel.InsightItem,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(text = insight.emoji, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = insight.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = insight.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private val CyberPurple = Color(0xFF7C4DFF)
