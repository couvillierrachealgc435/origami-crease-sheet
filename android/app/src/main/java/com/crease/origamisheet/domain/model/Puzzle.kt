package com.crease.origamisheet.domain.model

data class PuzzleTarget(
    val cells: Set<Cell>,
    val patterns: Map<Cell, Pattern>,
    val patterned: Boolean
)

data class Puzzle(
    val id: Int,
    val cols: Int,
    val par: Int,
    val patterned: Boolean,
    val start: Sheet,
    val target: PuzzleTarget,
    val solution: List<Fold>
)
