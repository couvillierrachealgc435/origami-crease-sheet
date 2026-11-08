package com.crease.origamisheet.presentation.tutorial

import androidx.lifecycle.ViewModel
import com.crease.origamisheet.core.config.GameConfig
import com.crease.origamisheet.domain.model.Cell
import com.crease.origamisheet.domain.model.Crease
import com.crease.origamisheet.domain.model.Fold
import com.crease.origamisheet.domain.model.Puzzle
import com.crease.origamisheet.domain.model.PuzzleTarget
import com.crease.origamisheet.domain.model.Sheet
import com.crease.origamisheet.domain.model.Side
import com.crease.origamisheet.domain.usecase.FoldUseCase
import com.crease.origamisheet.domain.usecase.GeneratePuzzleUseCase
import com.crease.origamisheet.domain.usecase.MatchUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TutorialUiState(
    val step: Int = 0,
    val cols: Int = 3,
    val sheet: Sheet = Sheet(3, emptyMap()),
    val target: PuzzleTarget = PuzzleTarget(emptySet(), emptyMap(), false),
    val creases: List<Crease> = emptyList(),
    val selected: Crease? = null,
    val blocked: Set<Cell> = emptySet(),
    val solved: Boolean = false,
    val isLastStep: Boolean = false
)

class TutorialViewModel : ViewModel() {

    private val _state = MutableStateFlow(TutorialUiState())
    val state: StateFlow<TutorialUiState> = _state.asStateFlow()

    private val steps: List<Puzzle> = List(GameConfig.TUTORIAL_STEPS) { index ->
        GeneratePuzzleUseCase.generate(
            id = TUTORIAL_ID - index,
            seed = TUTORIAL_SEED + index,
            cols = if (index >= 2) 4 else 3,
            parMin = if (index == 0) 1 else 2,
            parMax = if (index == 0) 1 else 2,
            patterned = index == 3
        )
    }

    init {
        loadStep(0)
    }

    private fun loadStep(index: Int) {
        val puzzle = steps[index.coerceIn(0, steps.lastIndex)]
        _state.update {
            it.copy(
                step = index,
                cols = puzzle.cols,
                sheet = puzzle.start,
                target = puzzle.target,
                creases = Crease.all(puzzle.cols),
                selected = null,
                blocked = emptySet(),
                solved = false,
                isLastStep = index == steps.lastIndex
            )
        }
    }

    fun onCreaseTap(crease: Crease) {
        val current = _state.value
        if (current.solved) return
        _state.update { it.copy(selected = if (it.selected == crease) null else crease) }
    }

    fun onFoldSelected() {
        val current = _state.value
        val crease = current.selected ?: return
        val fold = FoldUseCase.legalFold(current.sheet, crease)
        if (fold == null) {
            _state.update {
                it.copy(blocked = FoldUseCase.violations(current.sheet, Fold(crease, crease.sides.first())))
            }
            return
        }
        applyFold(fold)
    }

    fun onCreaseDrag(crease: Crease, side: Side) {
        applyFold(Fold(crease, side))
    }

    private fun applyFold(fold: Fold) {
        val current = _state.value
        if (current.solved) return
        if (!FoldUseCase.isLegal(current.sheet, fold)) {
            _state.update { it.copy(blocked = FoldUseCase.violations(current.sheet, fold), selected = null) }
            return
        }
        val after = FoldUseCase.apply(current.sheet, fold)
        _state.update {
            it.copy(
                sheet = after,
                selected = null,
                blocked = emptySet(),
                solved = MatchUseCase.matches(after, it.target)
            )
        }
    }

    fun onRetryStep() {
        loadStep(_state.value.step)
    }

    fun onNextStep() {
        val next = _state.value.step + 1
        if (next <= steps.lastIndex) loadStep(next)
    }

    fun onPreviousStep() {
        val previous = _state.value.step - 1
        if (previous >= 0) loadStep(previous)
    }

    private companion object {
        const val TUTORIAL_ID = -10
        const val TUTORIAL_SEED = 4242
    }
}
