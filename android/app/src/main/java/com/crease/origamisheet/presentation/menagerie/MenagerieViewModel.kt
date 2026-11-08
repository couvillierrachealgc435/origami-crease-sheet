package com.crease.origamisheet.presentation.menagerie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crease.origamisheet.data.sample.SampleData
import com.crease.origamisheet.domain.model.Cell
import com.crease.origamisheet.domain.model.SetAccent
import com.crease.origamisheet.domain.repository.ProgressRepository
import com.crease.origamisheet.domain.usecase.GeneratePuzzleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class AnimalUi(
    val index: Int,
    val nameRes: Int,
    val cols: Int,
    val collected: Boolean,
    val solved: Int,
    val accent: SetAccent,
    val silhouette: Set<Cell>,
    val foldedOn: String
)

data class MenagerieUiState(
    val loaded: Boolean = false,
    val collectedCount: Int = 0,
    val animals: List<AnimalUi> = emptyList(),
    val expanded: Int = -1
)

class MenagerieViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MenagerieUiState())
    val state: StateFlow<MenagerieUiState> = _state.asStateFlow()

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
            progressRepository.progress.collect { progress ->
                val animals = SampleData.drawers.map { drawer ->
                    val collected = progress.isCollected(drawer.index)
                    AnimalUi(
                        index = drawer.index,
                        nameRes = drawer.nameRes,
                        cols = drawer.cols,
                        collected = collected,
                        solved = progress.solvedInSet(drawer.index),
                        accent = drawer.accent,
                        silhouette = silhouettes[drawer.index].orEmpty(),
                        foldedOn = progress.animalDates[drawer.index]
                            ?.let { LocalDate.ofEpochDay(it).toString() }
                            .orEmpty()
                    )
                }
                _state.update {
                    it.copy(
                        loaded = true,
                        collectedCount = animals.count { animal -> animal.collected },
                        animals = animals
                    )
                }
            }
        }
    }

    fun onAnimalTap(index: Int) {
        _state.update { it.copy(expanded = if (it.expanded == index) -1 else index) }
    }
}
