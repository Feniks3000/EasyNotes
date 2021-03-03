package ru.geekbrains.easynotes.ui.note

import ru.geekbrains.easynotes.model.Note

data class Data(val isDeleted: Boolean = false, val note: Note? = null)