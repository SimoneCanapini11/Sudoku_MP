package com.canapini_grasselli.app_sudoku.views

//Formattazione del tempo
fun Int.toTimeString(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}