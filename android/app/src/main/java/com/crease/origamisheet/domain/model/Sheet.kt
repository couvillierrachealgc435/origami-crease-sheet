package com.crease.origamisheet.domain.model

data class Cell(val row: Int, val col: Int)

enum class Pattern { NONE, CRIMSON, TEAL }

data class Layer(val faceUp: Boolean, val pattern: Pattern)

data class Sheet(val cols: Int, val cells: Map<Cell, List<Layer>>) {

    val occupied: Set<Cell> get() = cells.keys

    fun stack(cell: Cell): List<Layer> = cells[cell] ?: emptyList()

    fun topPattern(cell: Cell): Pattern {
        val top = cells[cell]?.lastOrNull() ?: return Pattern.NONE
        return if (top.faceUp) top.pattern else Pattern.NONE
    }

    companion object {
        fun flat(cols: Int, patterned: Boolean): Sheet {
            val cells = LinkedHashMap<Cell, List<Layer>>()
            for (row in 0 until cols) {
                for (col in 0 until cols) {
                    val pattern = when {
                        !patterned -> Pattern.NONE
                        (row + col) % 2 == 0 -> Pattern.CRIMSON
                        else -> Pattern.TEAL
                    }
                    cells[Cell(row, col)] = listOf(Layer(faceUp = true, pattern = pattern))
                }
            }
            return Sheet(cols, cells)
        }
    }
}
