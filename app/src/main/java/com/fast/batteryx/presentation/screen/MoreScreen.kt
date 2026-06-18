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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fast.batteryx.presentation.components.GlassCard
import com.fast.batteryx.presentation.components.SectionHeader
import com.fast.batteryx.presentation.navigation.Screen
import com.fast.batteryx.ui.theme.ElectricBlue

@Composable
fun MoreScreen(
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(title = "Tools & Diagnostics")
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MoreMenuTile(
                    title = "Battery Assistant",
                    description = "Q&A chatbot that diagnoses your battery",
                    icon = Icons.Default.Psychology,
                    iconColor = ElectricBlue,
                    onClick = { onNavigate(Screen.Assistant) }
                )

                MoreMenuTile(
                    title = "Temperature History",
                    description = "Analyze battery heating and cooling logs",
                    icon = Icons.Default.DeviceThermostat,
                    iconColor = Color(0xFFFF8A65),
                    onClick = { onNavigate(Screen.Temperature) }
                )

                MoreMenuTile(
                    title = "Charging Habits",
                    description = "See if your charge cycles are healthy",
                    icon = Icons.Default.History,
                    iconColor = Color(0xFF81C784),
                    onClick = { onNavigate(Screen.Habits) }
                )

                MoreMenuTile(
                    title = "Lifetime Predictor",
                    description = "Forecast when battery will need replacement",
                    icon = Icons.Default.Timeline,
                    iconColor = Color(0xFFBA68C8),
                    onClick = { onNavigate(Screen.Predictor) }
                )
            }
        }

        item {
            SectionHeader(title = "Application Settings")
        }

        item {
            MoreMenuTile(
                title = "Settings",
                description = "Configure themes, alert thresholds, sounds & vibration",
                icon = Icons.Default.Settings,
                iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = { onNavigate(Screen.Settings) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun MoreMenuTile(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
