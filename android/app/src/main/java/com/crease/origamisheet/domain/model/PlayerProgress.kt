package com.crease.origamisheet.domain.model

import com.crease.origamisheet.core.config.GameConfig

data class PlayerProgress(
    val solved: Set<Int> = emptySet(),
    val best: Map<Int, Int> = emptyMap(),
    val animalDates: Map<Int, Long> = emptyMap(),
    val dailyStreak: Int = 0,
    val lastDailyEpochDay: Long = -1L
) {
    val totalSolved: Int get() = solved.size

    fun solvedInSet(setIndex: Int): Int = solved.count { it / 100 == setIndex }

    fun isSolved(puzzleId: Int): Boolean = solved.contains(puzzleId)

    fun bestFor(puzzleId: Int): Int = best[puzzleId] ?: 0

    fun isCollected(setIndex: Int): Boolean = solvedInSet(setIndex) >= GameConfig.PUZZLES_PER_SET

    fun isUnlocked(setIndex: Int): Boolean =
        setIndex == 0 || solvedInSet(setIndex - 1) >= GameConfig.UNLOCK_THRESHOLD

    fun firstUnsolved(): Pair<Int, Int> {
        for (setIndex in 0 until GameConfig.TOTAL_SETS) {
            if (!isUnlocked(setIndex)) break
            for (puzzleIndex in 0 until GameConfig.PUZZLES_PER_SET) {
                if (!isSolved(setIndex * 100 + puzzleIndex)) return setIndex to puzzleIndex
            }
        }
        return 0 to 0
    }
}
