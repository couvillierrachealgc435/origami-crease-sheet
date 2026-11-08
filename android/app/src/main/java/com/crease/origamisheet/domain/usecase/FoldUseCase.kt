package com.crease.origamisheet.domain.usecase

import com.crease.origamisheet.core.config.GameConfig
import com.crease.origamisheet.domain.model.Cell
import com.crease.origamisheet.domain.model.Crease
import com.crease.origamisheet.domain.model.Fold
import com.crease.origamisheet.domain.model.Layer
import com.crease.origamisheet.domain.model.Sheet

object FoldUseCase {

    fun movingCells(sheet: Sheet, fold: Fold): List<Cell> = sheet.occupied.filter { fold.moves(it) }

    fun violations(sheet: Sheet, fold: Fold): Set<Cell> {
        val moving = movingCells(sheet, fold)
        if (moving.isEmpty()) return emptySet()
        val range = 0 until sheet.cols
        val result = LinkedHashSet<Cell>()
        for (cell in moving) {
            val destination = fold.crease.mirror(cell)
            if (destination.row !in range || destination.col !in range) {
                result.add(cell)
                continue
            }
            val landing = if (fold.moves(destination)) emptyList() else sheet.stack(destination)
            if (landing.size + sheet.stack(cell).size > GameConfig.MAX_LAYERS) {
                result.add(destination)
            }
        }
        return result
    }

    fun isLegal(sheet: Sheet, fold: Fold): Boolean =
        movingCells(sheet, fold).isNotEmpty() && violations(sheet, fold).isEmpty()

    fun apply(sheet: Sheet, fold: Fold): Sheet {
        val result = LinkedHashMap<Cell, MutableList<Layer>>()
        for ((cell, layers) in sheet.cells) {
            if (!fold.moves(cell)) result[cell] = layers.toMutableList()
        }
        for ((cell, layers) in sheet.cells) {
            if (!fold.moves(cell)) continue
            val destination = fold.crease.mirror(cell)
            val flipped = layers.reversed().map { Layer(!it.faceUp, it.pattern) }
            result.getOrPut(destination) { mutableListOf() }.addAll(flipped)
        }
        return Sheet(sheet.cols, result.mapValues { it.value.toList() })
    }

    fun legalFold(sheet: Sheet, crease: Crease): Fold? =
        crease.sides.map { Fold(crease, it) }.firstOrNull { isLegal(sheet, it) }
}
