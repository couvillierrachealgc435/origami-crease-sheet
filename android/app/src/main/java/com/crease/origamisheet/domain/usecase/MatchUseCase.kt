package com.crease.origamisheet.domain.usecase

import com.crease.origamisheet.domain.model.Pattern
import com.crease.origamisheet.domain.model.PuzzleTarget
import com.crease.origamisheet.domain.model.Sheet

object MatchUseCase {

    fun matches(sheet: Sheet, target: PuzzleTarget): Boolean {
        if (target.cells.isEmpty()) return false
        if (sheet.occupied != target.cells) return false
        if (!target.patterned) return true
        return target.cells.all { sheet.topPattern(it) == (target.patterns[it] ?: Pattern.NONE) }
    }
}
