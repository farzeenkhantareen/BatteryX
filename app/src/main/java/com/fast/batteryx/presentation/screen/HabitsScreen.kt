package com.fast.batteryx.presentation.screen

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fast.batteryx.presentation.components.GlassCard
import com.fast.batteryx.presentation.components.SectionHeader
import com.fast.batteryx.presentation.viewmodel.HabitItem
import com.fast.batteryx.presentation.viewmodel.HabitSeverity
import com.fast.batteryx.presentation.viewmodel.HabitsViewModel
import com.fast.batteryx.ui.theme.ElectricBlue

@Composable
fun HabitsScreen(
    viewModel: HabitsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Habits Score Header Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Habit Quality Score",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val scoreColor = when {
                        uiState.habitScore >= 85f -> Color(0xFF00E676)
                        uiState.habitScore >= 65f -> Color(0xFFFFD740)
                        else -> Color(0xFFFF5252)
                    }

                    Text(
                        text = "${uiState.habitScore.toInt()}",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = scoreColor
                    )

                    val condition = when {
                        uiState.habitScore >= 85f -> "Excellent charging routine"
                        uiState.habitScore >= 65f -> "Minor room for improvement"
                        else -> "Degrading routines detected"
                    }

                    Text(
                        text = condition,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Habit Analysis Breakdown List
        item {
            SectionHeader(title = "Detections & Analysis")
        }

        items(uiState.habits) { habit ->
            HabitItemCard(habit = habit)
        }

        // Tips & Recommendations
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionHeader(title = "Longevity Recommendations")
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        uiState.recommendations.forEach { recommendation ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(ElectricBlue)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = recommendation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
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
fun HabitItemCard(
    habit: HabitItem,
    modifier: Modifier = Modifier
) {
    val severityColor = when (habit.severity) {
        HabitSeverity.GOOD -> Color(0xFF00E676)
        HabitSeverity.WARNING -> Color(0xFFFFD740)
        HabitSeverity.BAD -> Color(0xFFFF5252)
    }

    val severityIcon = when (habit.severity) {
        HabitSeverity.GOOD -> Icons.Default.CheckCircle
        HabitSeverity.WARNING -> Icons.Default.Info
        HabitSeverity.BAD -> Icons.Default.Warning
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
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
                Text(text = habit.emoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = habit.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = severityIcon,
                contentDescription = null,
                tint = severityColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
