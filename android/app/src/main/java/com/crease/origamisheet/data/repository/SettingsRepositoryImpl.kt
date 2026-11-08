package com.crease.origamisheet.data.repository

import com.crease.origamisheet.data.local.OrigamiPreferences
import com.crease.origamisheet.domain.model.GameSettings
import com.crease.origamisheet.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class SettingsRepositoryImpl(
    private val preferences: OrigamiPreferences
) : SettingsRepository {

    private val state = MutableStateFlow(GameSettings())
    private val mutex = Mutex()
    private var loaded = false

    override val settings: StateFlow<GameSettings> = state.asStateFlow()

    override suspend fun ensureLoaded() {
        mutex.withLock {
            if (loaded) return
            val stored = withContext(Dispatchers.IO) { preferences.readSettings() }
            loaded = true
            state.value = stored
        }
    }

    override suspend fun update(settings: GameSettings) {
        ensureLoaded()
        mutex.withLock {
            state.value = settings
            withContext(Dispatchers.IO) { preferences.writeSettings(settings) }
        }
    }
}
