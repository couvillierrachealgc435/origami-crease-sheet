package com.crease.origamisheet.presentation.game

import com.crease.origamisheet.R
import com.crease.origamisheet.core.config.GameConfig
import com.crease.origamisheet.domain.model.Cell
import com.crease.origamisheet.domain.model.Crease
import com.crease.origamisheet.domain.model.Fold
import com.crease.origamisheet.domain.model.GameSettings
import com.crease.origamisheet.domain.model.PuzzleTarget
import com.crease.origamisheet.domain.model.Sheet

enum class GamePhase { Loading, Idle, CreaseSelected, Folding, Blocked, Solved }

data class FoldAnimation(val fold: Fold, val after: Sheet, val progress: Float)

data class GameArgs(
    val setIndex: Int,
    val puzzleIndex: Int,
    val daily: Boolean
)

data class GameUiState(
    val phase: GamePhase = GamePhase.Loading,
    val cols: Int = 4,
    val sheet: Sheet = Sheet(4, emptyMap()),
    val target: PuzzleTarget = PuzzleTarget(emptySet(), emptyMap(), false),
    val creases: List<Crease> = emptyList(),
    val selected: Crease? = null,
    val hint: Crease? = null,
    val blocked: Set<Cell> = emptySet(),
    val animation: FoldAnimation? = null,
    val foldsUsed: Int = 0,
    val par: Int = 0,
    val hintsLeft: Int = GameConfig.HINTS_PER_PUZZLE,
    val hintOffPath: Boolean = false,
    val canUndo: Boolean = false,
    val setNameRes: Int = R.string.set_crane,
    val puzzleNumber: Int = 1,
    val daily: Boolean = false,
    val paused: Boolean = false,
    val matchPulse: Boolean = false,
    val settings: GameSettings = GameSettings()
) {
    val interactive: Boolean
        get() = phase == GamePhase.Idle || phase == GamePhase.CreaseSelected
}

sealed interface GameEvent {
    data class Finished(val folds: Int, val par: Int) : GameEvent
}
