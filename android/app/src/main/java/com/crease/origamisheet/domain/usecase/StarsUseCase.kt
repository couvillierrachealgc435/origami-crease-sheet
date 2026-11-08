package com.crease.origamisheet.domain.usecase

object StarsUseCase {

    fun stars(folds: Int, par: Int): Int = when {
        folds <= par -> 3
        folds <= par + 2 -> 2
        else -> 1
    }
}
