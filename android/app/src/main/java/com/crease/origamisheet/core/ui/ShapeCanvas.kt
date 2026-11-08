package com.crease.origamisheet.core.ui

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.crease.origamisheet.domain.model.Cell

fun DrawScope.drawShapeFill(
    cells: Set<Cell>,
    cols: Int,
    color: Color,
    corner: Float = 2f
) {
    if (cells.isEmpty() || cols <= 0) return
    val unit = size.minDimension / cols
    val originX = (size.width - unit * cols) / 2f
    val originY = (size.height - unit * cols) / 2f
    for (cell in cells) {
        drawRoundRect(
            color = color,
            topLeft = Offset(originX + cell.col * unit, originY + cell.row * unit),
            size = Size(unit, unit),
            cornerRadius = CornerRadius(corner, corner)
        )
    }
}

fun DrawScope.drawShapeOutline(
    cells: Set<Cell>,
    cols: Int,
    color: Color,
    strokeWidth: Float
) {
    if (cells.isEmpty() || cols <= 0) return
    val unit = size.minDimension / cols
    val originX = (size.width - unit * cols) / 2f
    val originY = (size.height - unit * cols) / 2f
    for (cell in cells) {
        val left = originX + cell.col * unit
        val top = originY + cell.row * unit
        val right = left + unit
        val bottom = top + unit
        if (Cell(cell.row - 1, cell.col) !in cells) {
            drawLine(color, Offset(left, top), Offset(right, top), strokeWidth)
        }
        if (Cell(cell.row + 1, cell.col) !in cells) {
            drawLine(color, Offset(left, bottom), Offset(right, bottom), strokeWidth)
        }
        if (Cell(cell.row, cell.col - 1) !in cells) {
            drawLine(color, Offset(left, top), Offset(left, bottom), strokeWidth)
        }
        if (Cell(cell.row, cell.col + 1) !in cells) {
            drawLine(color, Offset(right, top), Offset(right, bottom), strokeWidth)
        }
    }
}
