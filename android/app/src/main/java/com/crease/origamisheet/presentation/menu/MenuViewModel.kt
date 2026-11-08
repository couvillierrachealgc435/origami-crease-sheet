package com.crease.origamisheet.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crease.origamisheet.data.sample.SampleData
import com.crease.origamisheet.domain.model.PlayerProgress
import com.crease.origamisheet.domain.repository.ProgressRepository
import com.crease.origamisheet.domain.usecase.GeneratePuzzleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MenuViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    private val silhouettes = SampleData.drawers.associate { drawer ->
        drawer.index to GeneratePuzzleUseCase.generate(
            id = drawer.index * 100,
            seed = drawer.index * 1000,
            cols = drawer.cols,
            parMin = drawer.parMin,
            parMax = drawer.parMax,
            patterned = false
        ).target.cells
    }

    init {
        viewModelScope.launch {
            progressRepository.ensureLoaded()
            progressRepository.progress.collect { progress -> _state.value = build(progress) }
        }
    }

    private fun build(progress: PlayerProgress): MenuUiState {
        val resume = progress.firstUnsolved()
        return MenuUiState(
            loaded = true,
            solvedTotal = progress.totalSolved,
            streak = progress.dailyStreak,
            dailyDone = progress.lastDailyEpochDay == progressRepository.currentEpochDay(),
            drawers = SampleData.drawers.map { drawer ->
                DrawerUi(
                    index = drawer.index,
                    nameRes = drawer.nameRes,
                    cols = drawer.cols,
                    solved = progress.solvedInSet(drawer.index),
                    unlocked = progress.isUnlocked(drawer.index),
                    collected = progress.isCollected(drawer.index),
                    accent = drawer.accent,
                    silhouette = silhouettes[drawer.index].orEmpty()
                )
            },
            resumeSet = resume.first,
            resumePuzzle = resume.second
        )
    }
}
