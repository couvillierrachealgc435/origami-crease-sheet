package com.crease.origamisheet.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crease.origamisheet.domain.model.GameSettings
import com.crease.origamisheet.domain.model.OutlineContrast
import com.crease.origamisheet.domain.repository.ProgressRepository
import com.crease.origamisheet.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val settings: GameSettings = GameSettings(),
    val confirmVisible: Boolean = false,
    val solvedTotal: Int = 0
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.ensureLoaded()
            settingsRepository.settings.collect { settings ->
                _state.update { it.copy(settings = settings) }
            }
        }
        viewModelScope.launch {
            progressRepository.ensureLoaded()
            progressRepository.progress.collect { progress ->
                _state.update { it.copy(solvedTotal = progress.totalSolved) }
            }
        }
    }

    fun onHapticsChange(enabled: Boolean) {
        updateSettings { current -> current.copy(haptics = enabled) }
    }

    fun onSoundsChange(enabled: Boolean) {
        updateSettings { current -> current.copy(paperSounds = enabled) }
    }

    fun onMotionChange(enabled: Boolean) {
        updateSettings { current -> current.copy(reducedMotion = enabled) }
    }

    fun onContrastChange(contrast: OutlineContrast) {
        updateSettings { current -> current.copy(outlineContrast = contrast) }
    }

    fun onResetRequested() {
        _state.update { it.copy(confirmVisible = true) }
    }

    fun onResetDismissed() {
        _state.update { it.copy(confirmVisible = false) }
    }

    fun onResetConfirmed() {
        _state.update { it.copy(confirmVisible = false) }
        viewModelScope.launch { progressRepository.resetProgress() }
    }

    private fun updateSettings(transform: (GameSettings) -> GameSettings) {
        val updated = transform(_state.value.settings)
        _state.update { it.copy(settings = updated) }
        viewModelScope.launch { settingsRepository.update(updated) }
    }
}
