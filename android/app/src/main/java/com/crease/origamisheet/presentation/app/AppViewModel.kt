package com.crease.origamisheet.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crease.origamisheet.core.navigation.Screen
import com.crease.origamisheet.domain.model.GameSettings
import com.crease.origamisheet.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    val settings: StateFlow<GameSettings> = settingsRepository.settings

    private val backStack = mutableListOf<Screen>()

    init {
        viewModelScope.launch { settingsRepository.ensureLoaded() }
    }

    fun navigate(target: Screen, push: Boolean = true) {
        val current = _screen.value
        if (push && current !is Screen.Splash) backStack.add(current)
        _screen.value = target
    }

    fun openMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
    }

    fun back(): Boolean {
        val previous = backStack.removeLastOrNull() ?: return false
        _screen.value = previous
        return true
    }
}
