package com.crease.origamisheet.presentation.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.crease.origamisheet.core.config.GameConfig
import com.crease.origamisheet.core.theme.Crimson
import com.crease.origamisheet.core.theme.Ink
import com.crease.origamisheet.core.theme.PaperEdge
import com.crease.origamisheet.core.ui.drawShapeFill
import com.crease.origamisheet.core.ui.drawShapeOutline
import com.crease.origamisheet.domain.model.Cell
import com.crease.origamisheet.domain.model.OutlineContrast

@Composable
fun TargetOutline(
    cells: Set<Cell>,
    cols: Int,
    contrast: OutlineContrast,
    pulsing: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 84.dp
) {
    val pulse by animateFloatAsState(
        targetValue = if (pulsing) 1f else 0f,
        animationSpec = keyframes {
            durationMillis = GameConfig.PULSE_DURATION_MS.toInt()
            0f at 0
            1f at GameConfig.PULSE_DURATION_MS.toInt() / 2
            0f at GameConfig.PULSE_DURATION_MS.toInt()
        },
        label = "pulse"
    )
    val strokeDp = when (contrast) {
        OutlineContrast.LOW -> 1.5f
        OutlineContrast.NORMAL -> 2f
        OutlineContrast.HIGH -> 3f
    }
    val color = when (contrast) {
        OutlineContrast.LOW -> Crimson.copy(alpha = 0.7f)
        OutlineContrast.NORMAL -> Crimson
        OutlineContrast.HIGH -> Ink
    }
    Canvas(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                val factor = 1f + pulse * 0.06f
                scaleX = factor
                scaleY = factor
            }
    ) {
        drawShapeFill(cells, cols, PaperEdge.copy(alpha = 0.35f), corner = 1f)
        drawShapeOutline(cells, cols, color, strokeDp * density)
    }
}
