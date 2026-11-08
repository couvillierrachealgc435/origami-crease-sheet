package com.crease.origamisheet.domain.model

enum class Axis { VERTICAL, HORIZONTAL }

enum class Side { LEFT, RIGHT, TOP, BOTTOM }

data class Crease(val axis: Axis, val index: Int) {

    val sides: List<Side>
        get() = if (axis == Axis.VERTICAL) listOf(Side.LEFT, Side.RIGHT) else listOf(Side.TOP, Side.BOTTOM)

    fun mirror(cell: Cell): Cell = when (axis) {
        Axis.VERTICAL -> Cell(cell.row, 2 * index - 1 - cell.col)
        Axis.HORIZONTAL -> Cell(2 * index - 1 - cell.row, cell.col)
    }

    companion object {
        fun all(cols: Int): List<Crease> = (1 until cols).flatMap {
            listOf(Crease(Axis.VERTICAL, it), Crease(Axis.HORIZONTAL, it))
        }
    }
}
