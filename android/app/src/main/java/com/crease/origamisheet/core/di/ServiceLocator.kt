package com.crease.origamisheet.core.di

import android.content.Context
import com.crease.origamisheet.data.local.OrigamiPreferences
import com.crease.origamisheet.data.repository.ProgressRepositoryImpl
import com.crease.origamisheet.data.repository.SettingsRepositoryImpl
import com.crease.origamisheet.domain.repository.ProgressRepository
import com.crease.origamisheet.domain.repository.SettingsRepository

object ServiceLocator {

    private var progress: ProgressRepository? = null
    private var settings: SettingsRepository? = null

    fun init(context: Context) {
        if (progress != null && settings != null) return
        val preferences = OrigamiPreferences(context.applicationContext)
        progress = ProgressRepositoryImpl(preferences)
        settings = SettingsRepositoryImpl(preferences)
    }

    val progressRepository: ProgressRepository
        get() = checkNotNull(progress)

    val settingsRepository: SettingsRepository
        get() = checkNotNull(settings)
}
