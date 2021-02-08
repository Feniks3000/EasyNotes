package ru.geekbrains.easynotes.ui

import ru.geekbrains.easynotes.model.Note

class MainViewState(notes: List<Note>? = null, error: Throwable? = null) :
    BaseViewState<List<Note>?>(notes, error)