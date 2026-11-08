package com.crease.origamisheet.domain.model

enum class SetAccent { CRIMSON, TEAL, GOLD, INK }

data class AnimalSet(
    val index: Int,
    val nameRes: Int,
    val cols: Int,
    val parMin: Int,
    val parMax: Int,
    val accent: SetAccent
)
