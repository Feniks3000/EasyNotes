package ru.geekbrains.easynotes.model

import android.graphics.Color
import org.apache.commons.lang3.RandomStringUtils

object Repository {
    val notes: List<Note>

    init {
        notes = mutableListOf(
                Note("First note", RandomStringUtils.randomAlphabetic(50), Color.BLUE),
                Note("Second note", RandomStringUtils.randomAlphabetic(50)),
                Note("", RandomStringUtils.randomAlphabetic(50), Color.GRAY)
        )
    }
}