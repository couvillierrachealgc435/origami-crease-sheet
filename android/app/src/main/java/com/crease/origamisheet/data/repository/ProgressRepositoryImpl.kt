package com.crease.origamisheet.data.repository

import com.crease.origamisheet.core.config.GameConfig
import com.crease.origamisheet.data.local.OrigamiPreferences
import com.crease.origamisheet.domain.model.PlayerProgress
import com.crease.origamisheet.domain.repository.ProgressRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class ProgressRepositoryImpl(
    private val preferences: OrigamiPreferences
) : ProgressRepository {

    private val state = MutableStateFlow(PlayerProgress())
    private val mutex = Mutex()
    private var loaded = false

    override val progress: StateFlow<PlayerProgress> = state.asStateFlow()

    override fun currentEpochDay(): Long = System.currentTimeMillis() / MILLIS_PER_DAY

    override suspend fun ensureLoaded() {
        mutex.withLock {
            if (loaded) return
            val stored = withContext(Dispatchers.IO) { preferences.readProgress() }
            loaded = true
            state.value = stored
        }
    }

    override suspend fun recordSolve(puzzleId: Int, folds: Int) {
        ensureLoaded()
        mutex.withLock {
            val current = state.value
            val previousBest = current.best[puzzleId]
            val best = if (previousBest == null || folds < previousBest) folds else previousBest
            var updated = current.copy(
                solved = current.solved + puzzleId,
                best = current.best + (puzzleId to best)
            )
            val setIndex = puzzleId / 100
            if (updated.solvedInSet(setIndex) >= GameConfig.PUZZLES_PER_SET &&
                !updated.animalDates.containsKey(setIndex)
            ) {
                updated = updated.copy(
                    animalDates = updated.animalDates + (setIndex to currentEpochDay())
                )
            }
            persist(updated)
        }
    }

    override suspend fun recordDaily(folds: Int) {
        ensureLoaded()
        mutex.withLock {
            val current = state.value
            val today = currentEpochDay()
            if (current.lastDailyEpochDay == today) return
            val streak = if (current.lastDailyEpochDay == today - 1) current.dailyStreak + 1 else 1
            persist(current.copy(dailyStreak = streak, lastDailyEpochDay = today))
        }
    }

    override suspend fun resetProgress() {
        mutex.withLock {
            loaded = true
            state.value = PlayerProgress()
            withContext(Dispatchers.IO) { preferences.clearProgress() }
        }
    }

    private suspend fun persist(updated: PlayerProgress) {
        state.value = updated
        withContext(Dispatchers.IO) { preferences.writeProgress(updated) }
    }

    private companion object {
        const val MILLIS_PER_DAY = 86_400_000L
    }
}
