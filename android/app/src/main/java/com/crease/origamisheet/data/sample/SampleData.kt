package com.crease.origamisheet.data.sample

import com.crease.origamisheet.R
import com.crease.origamisheet.domain.model.AnimalSet
import com.crease.origamisheet.domain.model.SetAccent

object SampleData {

    val drawers: List<AnimalSet> = listOf(
        AnimalSet(0, R.string.set_crane, 4, 2, 3, SetAccent.CRIMSON),
        AnimalSet(1, R.string.set_frog, 4, 2, 3, SetAccent.TEAL),
        AnimalSet(2, R.string.set_fox, 4, 2, 3, SetAccent.CRIMSON),
        AnimalSet(3, R.string.set_butterfly, 5, 3, 4, SetAccent.GOLD),
        AnimalSet(4, R.string.set_rabbit, 5, 3, 4, SetAccent.INK),
        AnimalSet(5, R.string.set_koi, 5, 3, 4, SetAccent.TEAL),
        AnimalSet(6, R.string.set_tortoise, 5, 3, 4, SetAccent.CRIMSON),
        AnimalSet(7, R.string.set_swan, 6, 4, 5, SetAccent.INK),
        AnimalSet(8, R.string.set_cat, 6, 4, 5, SetAccent.GOLD),
        AnimalSet(9, R.string.set_elephant, 6, 4, 5, SetAccent.TEAL),
        AnimalSet(10, R.string.set_crab, 6, 4, 5, SetAccent.CRIMSON),
        AnimalSet(11, R.string.set_deer, 7, 5, 7, SetAccent.GOLD),
        AnimalSet(12, R.string.set_owl, 7, 5, 7, SetAccent.INK),
        AnimalSet(13, R.string.set_whale, 7, 5, 7, SetAccent.TEAL),
        AnimalSet(14, R.string.set_dragon, 7, 5, 7, SetAccent.CRIMSON)
    )

    val tutorialTitles: List<Int> = listOf(
        R.string.tutorial_step_1,
        R.string.tutorial_step_2,
        R.string.tutorial_step_3,
        R.string.tutorial_step_4
    )

    val tutorialBodies: List<Int> = listOf(
        R.string.tutorial_body_1,
        R.string.tutorial_body_2,
        R.string.tutorial_body_3,
        R.string.tutorial_body_4
    )

    fun drawer(index: Int): AnimalSet = drawers[index.coerceIn(0, drawers.lastIndex)]
}
