package com.crease.origamisheet.domain.model

data class Fold(val crease: Crease, val movingSide: Side) {

    fun moves(cell: Cell): Boolean = when (movingSide) {
        Side.LEFT -> cell.col < crease.index
        Side.RIGHT -> cell.col >= crease.index
        Side.TOP -> cell.row < crease.index
        Side.BOTTOM -> cell.row >= crease.index
    }
}
