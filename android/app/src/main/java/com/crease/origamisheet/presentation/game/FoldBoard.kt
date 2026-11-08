package com.crease.origamisheet.presentation.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.crease.origamisheet.core.config.GameConfig
import com.crease.origamisheet.core.theme.Crimson
import com.crease.origamisheet.core.theme.Ink
import com.crease.origamisheet.core.theme.PaperEdge
import com.crease.origamisheet.core.theme.PaperSurface
import com.crease.origamisheet.core.theme.Teal
import com.crease.origamisheet.core.theme.TealSoft
import com.crease.origamisheet.domain.model.Axis
import com.crease.origamisheet.domain.model.Cell
import com.crease.origamisheet.domain.model.Crease
import com.crease.origamisheet.domain.model.Layer
import com.crease.origamisheet.domain.model.Pattern
import com.crease.origamisheet.domain.model.Sheet
import com.crease.origamisheet.domain.model.Side
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FoldBoard(
    sheet: Sheet,
    cols: Int,
    creases: List<Crease>,
    selected: Crease?,
    hint: Crease?,
    blocked: Set<Cell>,
    animation: FoldAnimation?,
    interactive: Boolean,
    crossFade: Boolean,
    boardSize: Dp,
    cellSize: Dp,
    frameSize: Dp,
    onCreaseTap: (Crease) -> Unit,
    onCreaseFold: (Crease, Side) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val cellPx = with(density) { cellSize.toPx() }
    val framePx = with(density) { frameSize.toPx() }
    val touchPx = with(density) { GameConfig.CREASE_TOUCH_DP.dp.toPx() }
    val commitPx = with(density) { GameConfig.SWIPE_COMMIT_DP.dp.toPx() }
    val hairline = with(density) { 1.dp.toPx() }
    val radius = with(density) { 2.dp.toPx() }

    Canvas(
        modifier = modifier
            .size(boardSize)
            .pointerInput(cols, interactive, creases) {
                if (!interactive) return@pointerInput
                detectTapGestures { offset ->
                    val crease = nearestCrease(offset, creases, cellPx, framePx, touchPx)
                    if (crease != null) onCreaseTap(crease)
                }
            }
            .pointerInput(cols, interactive, creases) {
                if (!interactive) return@pointerInput
                var active: Crease? = null
                var startOffset = Offset.Zero
                var accumulated = Offset.Zero
                detectDragGestures(
                    onDragStart = { offset ->
                        startOffset = offset
                        accumulated = Offset.Zero
                        active = nearestCrease(offset, creases, cellPx, framePx, touchPx)
                    },
                    onDragEnd = {
                        val crease = active
                        if (crease != null) {
                            val side = resolveSide(
                                crease, startOffset, accumulated, cellPx, framePx, commitPx
                            )
                            if (side != null) onCreaseFold(crease, side)
                        }
                        active = null
                    },
                    onDragCancel = { active = null },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        accumulated += dragAmount
                    }
                )
            }
    ) {
        drawRoundRect(
            color = PaperSurface.copy(alpha = 0.92f),
            cornerRadius = CornerRadius(radius * 3, radius * 3)
        )
        drawRoundRect(
            color = PaperEdge,
            cornerRadius = CornerRadius(radius * 3, radius * 3),
            style = Stroke(width = hairline * 2)
        )
        val span = cellPx * cols
        for (index in 0..cols) {
            val shift = framePx + index * cellPx
            drawLine(
                color = PaperEdge.copy(alpha = 0.7f),
                start = Offset(shift, framePx),
                end = Offset(shift, framePx + span),
                strokeWidth = hairline
            )
            drawLine(
                color = PaperEdge.copy(alpha = 0.7f),
                start = Offset(framePx, shift),
                end = Offset(framePx + span, shift),
                strokeWidth = hairline
            )
        }

        val fold = animation?.fold
        for (entry in sheet.cells) {
            if (fold != null && fold.moves(entry.key)) continue
            drawCellStack(entry.key, entry.value, cellPx, framePx, radius, hairline, 1f)
        }

        val dash = PathEffect.dashPathEffect(floatArrayOf(hairline * 7, hairline * 6))
        for (crease in creases) {
            val position = framePx + crease.index * cellPx
            val isSelected = crease == selected
            val isHint = crease == hint
            val color = when {
                isSelected -> Teal
                isHint -> Teal
                else -> Teal.copy(alpha = 0.45f)
            }
            val width = if (isSelected || isHint) hairline * 2.5f else hairline * 1.5f
            if (isHint) {
                drawCreaseLine(crease, position, framePx, span, TealSoft.copy(alpha = 0.4f), hairline * 8, null)
            }
            drawCreaseLine(
                crease, position, framePx, span, color, width,
                if (isSelected || isHint) null else dash
            )
        }

        if (animation != null) {
            val moving = sheet.cells.filterKeys { animation.fold.moves(it) }
            val position = framePx + animation.fold.crease.index * cellPx
            if (crossFade) {
                for (entry in moving) {
                    drawCellStack(entry.key, entry.value, cellPx, framePx, radius, hairline, 1f - animation.progress)
                }
                for (entry in moving) {
                    val destination = animation.fold.crease.mirror(entry.key)
                    drawCellStack(
                        destination,
                        animation.after.stack(destination),
                        cellPx, framePx, radius, hairline, animation.progress
                    )
                }
            } else {
                val factor = cos(Math.PI * animation.progress).toFloat()
                val lift = sin(Math.PI * animation.progress).toFloat()
                withTransform({
                    if (animation.fold.crease.axis == Axis.VERTICAL) {
                        scale(factor, 1f, pivot = Offset(position, size.height / 2f))
                    } else {
                        scale(1f, factor, pivot = Offset(size.width / 2f, position))
                    }
                }) {
                    for (entry in moving) {
                        drawRoundRect(
                            color = Ink.copy(alpha = 0.16f * lift),
                            topLeft = Offset(
                                framePx + entry.key.col * cellPx + hairline * 6 * lift,
                                framePx + entry.key.row * cellPx + hairline * 8 * lift
                            ),
                            size = Size(cellPx, cellPx),
                            cornerRadius = CornerRadius(radius, radius)
                        )
                    }
                    for (entry in moving) {
                        drawCellStack(entry.key, entry.value, cellPx, framePx, radius, hairline, 1f)
                    }
                }
            }
        }

        for (cell in blocked) {
            drawRoundRect(
                color = Crimson.copy(alpha = 0.18f),
                topLeft = Offset(framePx + cell.col * cellPx, framePx + cell.row * cellPx),
                size = Size(cellPx, cellPx),
                cornerRadius = CornerRadius(radius, radius)
            )
        }
    }
}

private fun DrawScope.drawCreaseLine(
    crease: Crease,
    position: Float,
    framePx: Float,
    span: Float,
    color: Color,
    width: Float,
    effect: PathEffect?
) {
    if (crease.axis == Axis.VERTICAL) {
        drawLine(
            color = color,
            start = Offset(position, framePx),
            end = Offset(position, framePx + span),
            strokeWidth = width,
            pathEffect = effect
        )
    } else {
        drawLine(
            color = color,
            start = Offset(framePx, position),
            end = Offset(framePx + span, position),
            strokeWidth = width,
            pathEffect = effect
        )
    }
}

private fun DrawScope.drawCellStack(
    cell: Cell,
    layers: List<Layer>,
    cellPx: Float,
    framePx: Float,
    radius: Float,
    hairline: Float,
    alpha: Float
) {
    if (layers.isEmpty() || alpha <= 0f) return
    val left = framePx + cell.col * cellPx
    val top = framePx + cell.row * cellPx
    val depth = layers.size
    for (index in 0 until depth) {
        val shift = index * hairline
        drawRoundRect(
            color = Ink.copy(alpha = 0.10f * alpha),
            topLeft = Offset(left + shift + hairline * 2, top + shift + hairline * 2),
            size = Size(cellPx - shift, cellPx - shift),
            cornerRadius = CornerRadius(radius, radius)
        )
    }
    val inset = (depth - 1) * hairline
    val face = layers.last()
    val base = if (face.faceUp) PaperSurface else PaperEdge
    val innerLeft = left + inset
    val innerTop = top + inset
    val innerSize = Size(cellPx - inset * 2f, cellPx - inset * 2f)
    drawRoundRect(
        color = base.copy(alpha = alpha),
        topLeft = Offset(innerLeft, innerTop),
        size = innerSize,
        cornerRadius = CornerRadius(radius, radius)
    )
    if (face.faceUp && face.pattern != Pattern.NONE) {
        val tint = if (face.pattern == Pattern.CRIMSON) Crimson else Teal
        drawRoundRect(
            color = tint.copy(alpha = 0.22f * alpha),
            topLeft = Offset(innerLeft, innerTop),
            size = innerSize,
            cornerRadius = CornerRadius(radius, radius)
        )
    }
    val fibreSteps = 2
    for (index in 1..fibreSteps) {
        val y = innerTop + innerSize.height * index / (fibreSteps + 1f)
        drawLine(
            color = PaperEdge.copy(alpha = 0.3f * alpha),
            start = Offset(innerLeft + innerSize.width * 0.18f, y),
            end = Offset(innerLeft + innerSize.width * 0.78f, y),
            strokeWidth = hairline * 0.6f
        )
    }
    if (depth >= GameConfig.MAX_LAYERS) {
        drawRoundRect(
            color = Ink.copy(alpha = 0.25f * alpha),
            topLeft = Offset(innerLeft, innerTop),
            size = innerSize,
            cornerRadius = CornerRadius(radius, radius),
            style = Stroke(width = hairline)
        )
    }
}

private fun nearestCrease(
    offset: Offset,
    creases: List<Crease>,
    cellPx: Float,
    framePx: Float,
    touchPx: Float
): Crease? {
    var best: Crease? = null
    var bestDistance = touchPx / 2f
    for (crease in creases) {
        val position = framePx + crease.index * cellPx
        val distance = if (crease.axis == Axis.VERTICAL) {
            abs(offset.x - position)
        } else {
            abs(offset.y - position)
        }
        if (distance <= bestDistance) {
            bestDistance = distance
            best = crease
        }
    }
    return best
}

private fun resolveSide(
    crease: Crease,
    start: Offset,
    accumulated: Offset,
    cellPx: Float,
    framePx: Float,
    commitPx: Float
): Side? {
    val position = framePx + crease.index * cellPx
    return if (crease.axis == Axis.VERTICAL) {
        when {
            abs(accumulated.x) < commitPx -> null
            start.x < position && accumulated.x > 0f -> Side.LEFT
            start.x >= position && accumulated.x < 0f -> Side.RIGHT
            else -> null
        }
    } else {
        when {
            abs(accumulated.y) < commitPx -> null
            start.y < position && accumulated.y > 0f -> Side.TOP
            start.y >= position && accumulated.y < 0f -> Side.BOTTOM
            else -> null
        }
    }
}
