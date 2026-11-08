package com.crease.origamisheet.presentation.app

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crease.origamisheet.core.di.AppViewModelFactory
import com.crease.origamisheet.core.navigation.Screen
import com.crease.origamisheet.core.theme.PaperBackground
import com.crease.origamisheet.presentation.game.GameArgs
import com.crease.origamisheet.presentation.game.GameRoute
import com.crease.origamisheet.presentation.gameover.GameOverRoute
import com.crease.origamisheet.presentation.gameover.ResultArgs
import com.crease.origamisheet.presentation.menagerie.MenagerieRoute
import com.crease.origamisheet.presentation.menu.MenuRoute
import com.crease.origamisheet.presentation.settings.SettingsRoute
import com.crease.origamisheet.presentation.splash.SplashRoute
import com.crease.origamisheet.presentation.tutorial.TutorialRoute

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory())
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val settings by appViewModel.settings.collectAsStateWithLifecycle()
    val activity = LocalActivity.current

    BackHandler(enabled = screen !is Screen.Splash) {
        if (!appViewModel.back()) activity?.finish()
    }

    val duration = if (settings.reducedMotion) REDUCED_TRANSITION_MS else TRANSITION_MS

    AnimatedContent(
        targetState = screen,
        transitionSpec = {
            fadeIn(tween(duration)) togetherWith fadeOut(tween(duration * 2 / 3))
        },
        modifier = Modifier.fillMaxSize().background(PaperBackground),
        label = "screen"
    ) { current ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (current) {
                is Screen.Splash -> SplashRoute(onFinished = { appViewModel.openMenu() })

                is Screen.Menu -> MenuRoute(
                    onPlay = { setIndex, puzzleIndex ->
                        appViewModel.navigate(Screen.Game(setIndex, puzzleIndex))
                    },
                    onDaily = { appViewModel.navigate(Screen.Game(0, 0, daily = true)) },
                    onMenagerie = { appViewModel.navigate(Screen.Menagerie) },
                    onTutorial = { appViewModel.navigate(Screen.Tutorial) },
                    onSettings = { appViewModel.navigate(Screen.Settings) }
                )

                is Screen.Game -> GameRoute(
                    args = GameArgs(current.setIndex, current.puzzleIndex, current.daily),
                    attempt = current.attempt,
                    onMenu = { appViewModel.openMenu() },
                    onFinished = { folds, par ->
                        appViewModel.navigate(
                            target = Screen.GameOver(
                                setIndex = current.setIndex,
                                puzzleIndex = current.puzzleIndex,
                                daily = current.daily,
                                folds = folds,
                                par = par,
                                attempt = current.attempt
                            ),
                            push = false
                        )
                    }
                )

                is Screen.GameOver -> GameOverRoute(
                    args = ResultArgs(
                        setIndex = current.setIndex,
                        puzzleIndex = current.puzzleIndex,
                        daily = current.daily,
                        folds = current.folds,
                        par = current.par
                    ),
                    attempt = current.attempt,
                    onNext = { setIndex, puzzleIndex ->
                        appViewModel.navigate(
                            target = Screen.Game(setIndex, puzzleIndex),
                            push = false
                        )
                    },
                    onReplayPuzzle = {
                        appViewModel.navigate(
                            target = Screen.Game(
                                setIndex = current.setIndex,
                                puzzleIndex = current.puzzleIndex,
                                daily = current.daily,
                                attempt = current.attempt + 1
                            ),
                            push = false
                        )
                    },
                    onMenu = { appViewModel.openMenu() }
                )

                is Screen.Menagerie -> MenagerieRoute(
                    onHome = { appViewModel.openMenu() },
                    onOpenDrawer = { setIndex ->
                        appViewModel.navigate(Screen.Game(setIndex, 0))
                    }
                )

                is Screen.Tutorial -> TutorialRoute(onHome = { appViewModel.openMenu() })

                is Screen.Settings -> SettingsRoute(onHome = { appViewModel.openMenu() })
            }
        }
    }
}

private const val TRANSITION_MS = 320
private const val REDUCED_TRANSITION_MS = 120
