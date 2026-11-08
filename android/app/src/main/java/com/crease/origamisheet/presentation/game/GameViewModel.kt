package com.crease.origamisheet.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crease.origamisheet.core.config.GameConfig
import com.crease.origamisheet.data.sample.SampleData
import com.crease.origamisheet.domain.model.Cell
import com.crease.origamisheet.domain.model.Crease
import com.crease.origamisheet.domain.model.Fold
import com.crease.origamisheet.domain.model.Puzzle
import com.crease.origamisheet.domain.model.Sheet
import com.crease.origamisheet.domain.model.Side
import com.crease.origamisheet.domain.repository.ProgressRepository
import com.crease.origamisheet.domain.repository.SettingsRepository
import com.crease.origamisheet.domain.usecase.FoldUseCase
import com.crease.origamisheet.domain.usecase.GeneratePuzzleUseCase
import com.crease.origamisheet.domain.usecase.HintUseCase
import com.crease.origamisheet.domain.usecase.MatchUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameViewModel(
    private val args: GameArgs,
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val _events = Channel<GameEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var puzzle: Puzzle? = null
    private val history = mutableListOf<Sheet>()
    private val applied = mutableListOf<Fold>()
    private var foldJob: Job? = null
    private var blockJob: Job? = null
    private var hintJob: Job? = null

    init {
        viewModelScope.launch {
            settingsRepository.ensureLoaded()
            settingsRepository.settings.collect { settings ->
                _state.update { it.copy(settings = settings) }
            }
        }
        load()
    }

    private fun load() {
        val drawer = SampleData.drawer(args.setIndex)
        val generated = if (args.daily) {
            GeneratePuzzleUseCase.generate(
                id = DAILY_ID,
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
        puzzle = generated
        history.clear()
        applied.clear()
        _state.update {
            it.copy(
                phase = GamePhase.Idle,
                cols = generated.cols,
                sheet = generated.start,
                target = generated.target,
                creases = Crease.all(generated.cols),
                selected = null,
                hint = null,
                blocked = emptySet(),
                animation = null,
                foldsUsed = 0,
                par = generated.par,
                hintsLeft = GameConfig.HINTS_PER_PUZZLE,
                hintOffPath = false,
                canUndo = false,
                setNameRes = drawer.nameRes,
                puzzleNumber = args.puzzleIndex + 1,
                daily = args.daily,
                matchPulse = false
            )
        }
    }

    fun onCreaseTap(crease: Crease) {
        val current = _state.value
        if (!current.interactive) return
        val next = if (current.selected == crease) null else crease
        _state.update {
            it.copy(
                selected = next,
                hintOffPath = false,
                phase = if (next == null) GamePhase.Idle else GamePhase.CreaseSelected
            )
        }
    }

    fun onFoldSelected() {
        val current = _state.value
        val crease = current.selected ?: return
        val fold = FoldUseCase.legalFold(current.sheet, crease)
        if (fold == null) {
            flashBlocked(FoldUseCase.violations(current.sheet, Fold(crease, crease.sides.first())))
            return
        }
        startFold(fold)
    }

    fun onCreaseDrag(crease: Crease, side: Side) {
        if (!_state.value.interactive) return
        startFold(Fold(crease, side))
    }

    fun onUndo() {
        val current = _state.value
        if (!current.interactive || history.isEmpty()) return
        val previous = history.removeAt(history.lastIndex)
        if (applied.isNotEmpty()) applied.removeAt(applied.lastIndex)
        _state.update {
            it.copy(
                sheet = previous,
                selected = null,
                hint = null,
                blocked = emptySet(),
                foldsUsed = applied.size,
                canUndo = history.isNotEmpty(),
                hintOffPath = false,
                phase = GamePhase.Idle
            )
        }
    }

    fun onReset() {
        val current = puzzle ?: return
        if (!_state.value.interactive) return
        history.clear()
        applied.clear()
        _state.update {
            it.copy(
                sheet = current.start,
                selected = null,
                hint = null,
                blocked = emptySet(),
                foldsUsed = 0,
                canUndo = false,
                hintOffPath = false,
                phase = GamePhase.Idle
            )
        }
    }

    fun onHint() {
        val current = puzzle ?: return
        val snapshot = _state.value
        if (!snapshot.interactive || snapshot.hintsLeft <= 0) return
        val next = HintUseCase.next(current, applied)
        if (next == null) {
            _state.update { it.copy(hintOffPath = true, hint = null) }
            return
        }
        _state.update {
            it.copy(hint = next.crease, hintsLeft = it.hintsLeft - 1, hintOffPath = false)
        }
        hintJob?.cancel()
        hintJob = viewModelScope.launch {
            delay(GameConfig.HINT_GLOW_MS)
            _state.update { it.copy(hint = null) }
        }
    }

    fun onPauseToggle(paused: Boolean) {
        _state.update { it.copy(paused = paused) }
    }

    fun onRestart() {
        _state.update { it.copy(paused = false) }
        onReset()
    }

    fun pause() {
        foldJob?.cancel()
        blockJob?.cancel()
        hintJob?.cancel()
    }

    override fun onCleared() {
        pause()
        super.onCleared()
    }

    private fun startFold(fold: Fold) {
        val current = puzzle ?: return
        val before = _state.value.sheet
        if (!FoldUseCase.isLegal(before, fold)) {
            flashBlocked(FoldUseCase.violations(before, fold))
            return
        }
        val after = FoldUseCase.apply(before, fold)
        val duration = if (_state.value.settings.reducedMotion) {
            GameConfig.REDUCED_FOLD_MS
        } else {
            GameConfig.FOLD_DURATION_MS
        }
        _state.update {
            it.copy(
                phase = GamePhase.Folding,
                selected = null,
                hint = null,
                blocked = emptySet(),
                hintOffPath = false,
                animation = FoldAnimation(fold, after, 0f)
            )
        }
        foldJob?.cancel()
        foldJob = viewModelScope.launch {
            val startedAt = System.currentTimeMillis()
            while (isActive) {
                val elapsed = (System.currentTimeMillis() - startedAt).toFloat()
                val progress = (elapsed / duration).coerceIn(0f, 1f)
                _state.update { snapshot ->
                    snapshot.copy(animation = snapshot.animation?.copy(progress = progress))
                }
                if (progress >= 1f) break
                delay(FRAME_MS)
            }
            commit(before, fold, after, current)
        }
    }

    private suspend fun commit(before: Sheet, fold: Fold, after: Sheet, current: Puzzle) {
        history.add(before)
        applied.add(fold)
        val solved = MatchUseCase.matches(after, current.target)
        _state.update {
            it.copy(
                sheet = after,
                animation = null,
                foldsUsed = applied.size,
                canUndo = true,
                matchPulse = solved,
                phase = if (solved) GamePhase.Solved else GamePhase.Idle
            )
        }
        if (!solved) return
        if (args.daily) {
            progressRepository.recordDaily(applied.size)
        } else {
            progressRepository.recordSolve(args.setIndex * 100 + args.puzzleIndex, applied.size)
        }
        delay(GameConfig.WIN_SETTLE_MS)
        _events.send(GameEvent.Finished(applied.size, current.par))
    }

    private fun flashBlocked(cells: Set<Cell>) {
        _state.update { it.copy(phase = GamePhase.Blocked, blocked = cells) }
        blockJob?.cancel()
        blockJob = viewModelScope.launch {
            delay(GameConfig.BLOCK_FLASH_MS)
            _state.update {
                it.copy(
                    phase = if (it.selected == null) GamePhase.Idle else GamePhase.CreaseSelected,
                    blocked = emptySet()
                )
            }
        }
    }

    private companion object {
        const val FRAME_MS = 16L
        const val DAILY_ID = -1
    }
}
