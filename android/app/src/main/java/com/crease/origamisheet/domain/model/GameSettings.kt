package com.crease.origamisheet.domain.model

enum class OutlineContrast { LOW, NORMAL, HIGH }

data class GameSettings(
    val haptics: Boolean = true,
    val paperSounds: Boolean = true,
    val reducedMotion: Boolean = false,
    val outlineContrast: OutlineContrast = OutlineContrast.NORMAL
)
