package com.crease.origamisheet.core.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import com.crease.origamisheet.core.theme.PaperEdge
import kotlin.random.Random

fun Modifier.paperFibre(seed: Int = 7, strokes: Int = 12): Modifier = this.drawBehind {
    val random = Random(seed)
    repeat(strokes) {
        val x = random.nextFloat() * size.width
        val y = random.nextFloat() * size.height
        val length = size.width * (0.12f + random.nextFloat() * 0.26f)
        drawLine(
            color = PaperEdge.copy(alpha = 0.06f),
            start = Offset(x, y),
            end = Offset((x + length).coerceAtMost(size.width), y),
            strokeWidth = 1f
        )
    }
}
