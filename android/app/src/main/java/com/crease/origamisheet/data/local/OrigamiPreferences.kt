package com.crease.origamisheet.data.local

import android.content.Context
import android.content.SharedPreferences
import com.crease.origamisheet.domain.model.GameSettings
import com.crease.origamisheet.domain.model.OutlineContrast
import com.crease.origamisheet.domain.model.PlayerProgress

class OrigamiPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun readProgress(): PlayerProgress = PlayerProgress(
        solved = decodeInts(prefs.getString(KEY_SOLVED, "")),
        best = decodeIntMap(prefs.getString(KEY_BEST, "")),
        animalDates = decodeLongMap(prefs.getString(KEY_ANIMALS, "")),
        dailyStreak = prefs.getInt(KEY_STREAK, 0),
        lastDailyEpochDay = prefs.getLong(KEY_LAST_DAILY, -1L)
    )

    fun writeProgress(progress: PlayerProgress) {
        prefs.edit()
            .putString(KEY_SOLVED, progress.solved.joinToString(SEPARATOR))
            .putString(KEY_BEST, encodeIntMap(progress.best))
            .putString(KEY_ANIMALS, encodeLongMap(progress.animalDates))
            .putInt(KEY_STREAK, progress.dailyStreak)
            .putLong(KEY_LAST_DAILY, progress.lastDailyEpochDay)
            .apply()
    }

    fun clearProgress() {
        prefs.edit()
            .remove(KEY_SOLVED)
            .remove(KEY_BEST)
            .remove(KEY_ANIMALS)
            .remove(KEY_STREAK)
            .remove(KEY_LAST_DAILY)
            .apply()
    }

    fun readSettings(): GameSettings = GameSettings(
        haptics = prefs.getBoolean(KEY_HAPTICS, true),
        paperSounds = prefs.getBoolean(KEY_SOUNDS, true),
        reducedMotion = prefs.getBoolean(KEY_MOTION, false),
        outlineContrast = decodeContrast(prefs.getString(KEY_CONTRAST, null))
    )

    fun writeSettings(settings: GameSettings) {
        prefs.edit()
            .putBoolean(KEY_HAPTICS, settings.haptics)
            .putBoolean(KEY_SOUNDS, settings.paperSounds)
            .putBoolean(KEY_MOTION, settings.reducedMotion)
            .putString(KEY_CONTRAST, settings.outlineContrast.name)
            .apply()
    }

    private fun decodeContrast(raw: String?): OutlineContrast =
        OutlineContrast.entries.firstOrNull { it.name == raw } ?: OutlineContrast.NORMAL

    private fun decodeInts(raw: String?): Set<Int> =
        raw.orEmpty().split(SEPARATOR).mapNotNull { it.toIntOrNull() }.toSet()

    private fun decodeIntMap(raw: String?): Map<Int, Int> =
        raw.orEmpty().split(SEPARATOR).mapNotNull { entry ->
            val parts = entry.split(PAIR)
            val key = parts.getOrNull(0)?.toIntOrNull()
            val value = parts.getOrNull(1)?.toIntOrNull()
            if (key == null || value == null) null else key to value
        }.toMap()

    private fun decodeLongMap(raw: String?): Map<Int, Long> =
        raw.orEmpty().split(SEPARATOR).mapNotNull { entry ->
            val parts = entry.split(PAIR)
            val key = parts.getOrNull(0)?.toIntOrNull()
            val value = parts.getOrNull(1)?.toLongOrNull()
            if (key == null || value == null) null else key to value
        }.toMap()

    private fun encodeIntMap(map: Map<Int, Int>): String =
        map.entries.joinToString(SEPARATOR) { it.key.toString() + PAIR + it.value.toString() }

    private fun encodeLongMap(map: Map<Int, Long>): String =
        map.entries.joinToString(SEPARATOR) { it.key.toString() + PAIR + it.value.toString() }

    private companion object {
        const val FILE_NAME = "origami_crease_sheet"
        const val SEPARATOR = ","
        const val PAIR = ":"
        const val KEY_SOLVED = "solved_puzzles"
        const val KEY_BEST = "best_folds"
        const val KEY_ANIMALS = "animal_dates"
        const val KEY_STREAK = "daily_streak"
        const val KEY_LAST_DAILY = "last_daily_epoch_day"
        const val KEY_HAPTICS = "haptics"
        const val KEY_SOUNDS = "paper_sounds"
        const val KEY_MOTION = "reduced_motion"
        const val KEY_CONTRAST = "outline_contrast"
    }
}
