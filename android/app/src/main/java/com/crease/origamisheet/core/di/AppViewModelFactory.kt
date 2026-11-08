package com.crease.origamisheet.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.crease.origamisheet.presentation.app.AppViewModel
import com.crease.origamisheet.presentation.game.GameArgs
import com.crease.origamisheet.presentation.game.GameViewModel
import com.crease.origamisheet.presentation.gameover.GameOverViewModel
import com.crease.origamisheet.presentation.gameover.ResultArgs
import com.crease.origamisheet.presentation.menagerie.MenagerieViewModel
import com.crease.origamisheet.presentation.menu.MenuViewModel
import com.crease.origamisheet.presentation.settings.SettingsViewModel
import com.crease.origamisheet.presentation.splash.SplashViewModel
import com.crease.origamisheet.presentation.tutorial.TutorialViewModel

class AppViewModelFactory(private val args: Any? = null) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val progress = ServiceLocator.progressRepository
        val settings = ServiceLocator.settingsRepository
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> AppViewModel(settings)
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel()
            modelClass.isAssignableFrom(MenuViewModel::class.java) -> MenuViewModel(progress)
            modelClass.isAssignableFrom(GameViewModel::class.java) ->
                GameViewModel(args as GameArgs, progress, settings)
            modelClass.isAssignableFrom(GameOverViewModel::class.java) ->
                GameOverViewModel(args as ResultArgs, progress)
            modelClass.isAssignableFrom(MenagerieViewModel::class.java) -> MenagerieViewModel(progress)
            modelClass.isAssignableFrom(TutorialViewModel::class.java) -> TutorialViewModel()
            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(settings, progress)
            else -> throw IllegalArgumentException(modelClass.name)
        }
        return created as T
    }
}
