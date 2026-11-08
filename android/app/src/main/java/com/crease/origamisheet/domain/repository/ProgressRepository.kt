package com.crease.origamisheet.domain.repository

import com.crease.origamisheet.domain.model.PlayerProgress
import kotlinx.coroutines.flow.StateFlow

interface ProgressRepository {

    val progress: StateFlow<PlayerProgress>

    fun currentEpochDay(): Long

    suspend fun ensureLoaded()

    suspend fun recordSolve(puzzleId: Int, folds: Int)

    suspend fun recordDaily(folds: Int)

    suspend fun resetProgress()
}
