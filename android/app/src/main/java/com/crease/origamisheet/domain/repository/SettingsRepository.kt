package com.crease.origamisheet.domain.repository

import com.crease.origamisheet.domain.model.GameSettings
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {

    val settings: StateFlow<GameSettings>

    suspend fun ensureLoaded()

    suspend fun update(settings: GameSettings)
}
