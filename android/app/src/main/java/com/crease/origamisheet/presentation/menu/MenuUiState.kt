package com.crease.origamisheet.presentation.menu

import com.crease.origamisheet.domain.model.Cell
import com.crease.origamisheet.domain.model.SetAccent

data class DrawerUi(
    val index: Int,
    val nameRes: Int,
    val cols: Int,
    val solved: Int,
    val unlocked: Boolean,
    val collected: Boolean,
    val accent: SetAccent,
    val silhouette: Set<Cell>
)

data class MenuUiState(
    val loaded: Boolean = false,
    val solvedTotal: Int = 0,
    val streak: Int = 0,
    val dailyDone: Boolean = false,
    val drawers: List<DrawerUi> = emptyList(),
    val resumeSet: Int = 0,
    val resumePuzzle: Int = 0
)
