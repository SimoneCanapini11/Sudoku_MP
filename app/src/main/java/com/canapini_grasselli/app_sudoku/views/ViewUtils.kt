package com.canapini_grasselli.app_sudoku.views

import com.canapini_grasselli.app_sudoku.R

//Formattazione del tempo
fun Int.toTimeString(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}

fun String.toDifficultyStringResource(): Int {
    return when (lowercase()) {
        "easy" -> R.string.easy_difficulty
        "medium" -> R.string.medium_difficulty
        "hard" -> R.string.hard_difficulty
        else -> R.string.difficulty_unknown
    }
}

fun Int.fromDifficultyResourceToString(): String {
    return when (this) {
        R.string.easy_difficulty -> "easy"
        R.string.medium_difficulty -> "medium"
        R.string.hard_difficulty -> "hard"
        else -> "unknown"
    }
}