package com.crease.origamisheet.domain.usecase

import com.crease.origamisheet.domain.model.Fold
import com.crease.origamisheet.domain.model.Puzzle

object HintUseCase {

    fun next(puzzle: Puzzle, applied: List<Fold>): Fold? {
        if (applied.size >= puzzle.solution.size) return null
        if (puzzle.solution.take(applied.size) != applied) return null
        return puzzle.solution[applied.size]
    }
}
