package com.crease.origamisheet.presentation.menu

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.crease.origamisheet.core.config.GameConfig
import com.crease.origamisheet.core.theme.PaperEdge
import com.crease.origamisheet.core.theme.Teal

@Composable
fun StreakRing(streak: Int, modifier: Modifier = Modifier) {
    val fraction = (streak.toFloat() / GameConfig.DAILY_STREAK_RING_MAX).coerceIn(0f, 1f)
    val sweep by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(600),
        label = "streak"
    )
    Box(modifier = modifier.size(46.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(46.dp)) {
            val stroke = 3.dp.toPx()
            val inset = stroke / 2f
            drawArc(
                color = PaperEdge,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(size.width - stroke, size.height - stroke),
                style = Stroke(width = stroke)
            )
            drawArc(
                color = Teal,
                startAngle = -90f,
                sweepAngle = 360f * sweep,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(size.width - stroke, size.height - stroke),
                style = Stroke(width = stroke)
            )
        }
        Text(
            text = streak.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = Teal
        )
    }
}
