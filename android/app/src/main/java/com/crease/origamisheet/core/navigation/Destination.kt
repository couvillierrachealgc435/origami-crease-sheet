package com.crease.origamisheet.core.navigation

sealed interface Screen {

    data object Splash : Screen

    data object Menu : Screen

    data class Game(
        val setIndex: Int,
        val puzzleIndex: Int,
        val daily: Boolean = false,
        val attempt: Int = 0
    ) : Screen

    data class GameOver(
        val setIndex: Int,
        val puzzleIndex: Int,
        val daily: Boolean,
        val folds: Int,
        val par: Int,
        val attempt: Int
    ) : Screen

    data object Menagerie : Screen

    data object Tutorial : Screen

    data object Settings : Screen
}
