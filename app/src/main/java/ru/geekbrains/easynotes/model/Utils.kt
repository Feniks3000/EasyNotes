package ru.geekbrains.easynotes.model

import ru.geekbrains.easynotes.R

fun getColorInt(color: Color? = Color.WHITE): Int =
    when (color) {
        Color.WHITE -> R.color.color_white
        Color.VIOLET -> R.color.color_violet
        Color.YELLOW -> R.color.color_yellow
        Color.RED -> R.color.color_red
        Color.PINK -> R.color.color_pink
        Color.GREEN -> R.color.color_green
        Color.BLUE -> R.color.color_blue
        Color.GRAY -> R.color.color_gray
        else -> R.color.white
    }