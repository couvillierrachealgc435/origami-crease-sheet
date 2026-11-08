package com.crease.origamisheet.domain.usecase

import com.crease.origamisheet.domain.model.Crease
import com.crease.origamisheet.domain.model.Fold
import com.crease.origamisheet.domain.model.Puzzle
import com.crease.origamisheet.domain.model.PuzzleTarget
import com.crease.origamisheet.domain.model.Sheet
import kotlin.random.Random

object GeneratePuzzleUseCase {

    fun generate(
        id: Int,
        seed: Int,
        cols: Int,
        parMin: Int,
        parMax: Int,
        patterned: Boolean
    ): Puzzle {
        val random = Random(seed)
        val start = Sheet.flat(cols, patterned)
        val wanted = parMin + random.nextInt((parMax - parMin + 1).coerceAtLeast(1))
        val creases = Crease.all(cols)
        val solution = mutableListOf<Fold>()
        var sheet = start
        var guard = 0
        while (solution.size < wanted && guard < 120) {
            guard++
            val options = creases
                .flatMap { crease -> crease.sides.map { Fold(crease, it) } }
                .filter { FoldUseCase.isLegal(sheet, it) }
            if (options.isEmpty()) break
            val fold = options[random.nextInt(options.size)]
            sheet = FoldUseCase.apply(sheet, fold)
            solution.add(fold)
        }
        val patterns = sheet.occupied.associateWith { sheet.topPattern(it) }
        return Puzzle(
            id = id,
            cols = cols,
            par = solution.size.coerceAtLeast(1),
            patterned = patterned,
            start = start,
            target = PuzzleTarget(sheet.occupied, patterns, patterned),
            solution = solution
        )
    }
}
