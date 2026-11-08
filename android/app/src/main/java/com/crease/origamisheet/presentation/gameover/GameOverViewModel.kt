package com.crease.origamisheet.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crease.origamisheet.R
import com.crease.origamisheet.core.config.GameConfig
import com.crease.origamisheet.data.sample.SampleData
import com.crease.origamisheet.domain.model.Cell
import com.crease.origamisheet.domain.model.Sheet
import com.crease.origamisheet.domain.repository.ProgressRepository
import com.crease.origamisheet.domain.usecase.FoldUseCase
import com.crease.origamisheet.domain.usecase.GeneratePuzzleUseCase
import com.crease.origamisheet.domain.usecase.StarsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ResultArgs(
    val setIndex: Int,
    val puzzleIndex: Int,
    val daily: Boolean,
    val folds: Int,
    val par: Int
)

data class GameOverUiState(
    val loaded: Boolean = false,
    val setNameRes: Int = R.string.set_crane,
    val puzzleNumber: Int = 1,
    val daily: Boolean = false,
    val folds: Int = 0,
    val par: Int = 0,
    val best: Int = 0,
    val stars: Int = 1,
    val setCompleted: Boolean = false,
    val hasNext: Boolean = true,
    val streak: Int = 0,
    val cols: Int = 4,
    val frames: List<Sheet> = emptyList(),
    val frameIndex: Int = 0,
    val animalCells: Set<Cell> = emptySet()
)

class GameOverViewModel(
    private val args: ResultArgs,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    private var replayJob: Job? = null

    init {
        load()
        startReplay()
    }

    private fun load() {
        val drawer = SampleData.drawer(args.setIndex)
        val puzzle = if (args.daily) {
            GeneratePuzzleUseCase.generate(
                id = -1,
                seed = progressRepository.currentEpochDay().toInt(),
                cols = GameConfig.DAILY_COLS,
                parMin = GameConfig.DAILY_PAR,
                parMax = GameConfig.DAILY_PAR,
                patterned = false
            )
        } else {
            GeneratePuzzleUseCase.generate(
                id = args.setIndex * 100 + args.puzzleIndex,
                seed = args.setIndex * 1000 + args.puzzleIndex,
                cols = drawer.cols,
                parMin = drawer.parMin,
                parMax = drawer.parMax,
                patterned = args.setIndex >= GameConfig.PATTERN_FROM_SET
            )
        }
        val frames = mutableListOf(puzzle.start)
        var sheet = puzzle.start
        for (fold in puzzle.solution) {
            sheet = FoldUseCase.apply(sheet, fold)
            frames.add(sheet)
        }
        val lastSet = args.setIndex >= GameConfig.TOTAL_SETS - 1
        val lastPuzzle = args.puzzleIndex >= GameConfig.PUZZLES_PER_SET - 1
        _state.update {
            it.copy(
                loaded = true,
                setNameRes = drawer.nameRes,
                puzzleNumber = args.puzzleIndex + 1,
                daily = args.daily,
                folds = args.folds,
                par = args.par,
                stars = StarsUseCase.stars(args.folds, args.par),
                hasNext = !args.daily && !(lastSet && lastPuzzle),
                cols = puzzle.cols,
                frames = frames,
                frameIndex = 0,
                animalCells = puzzle.target.cells
            )
        }
        viewModelScope.launch {
            progressRepository.ensureLoaded()
            progressRepository.progress.collect { progress ->
                _state.update {
                    it.copy(
                        best = if (args.daily) 0 else progress.bestFor(
                            args.setIndex * 100 + args.puzzleIndex
                        ),
                        setCompleted = !args.daily && progress.isCollected(args.setIndex),
                        streak = progress.dailyStreak
                    )
                }
            }
        }
    }

    fun startReplay() {
        replayJob?.cancel()
        replayJob = viewModelScope.launch {
            _state.update { it.copy(frameIndex = 0) }
            val total = _state.value.frames.size
            for (index in 1 until total) {
                delay(REPLAY_STEP_MS)
                _state.update { it.copy(frameIndex = index) }
            }
        }
    }

    fun pause() {
        replayJob?.cancel()
    }

    override fun onCleared() {
        pause()
        super.onCleared()
    }

    private companion object {
        const val REPLAY_STEP_MS = 220L
    }
}
