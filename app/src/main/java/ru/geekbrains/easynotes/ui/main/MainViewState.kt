package ru.geekbrains.easynotes.ui.main

import ru.geekbrains.easynotes.model.Note
import ru.geekbrains.easynotes.ui.base.BaseViewState

class MainViewState(notes: List<Note>? = null, error: Throwable? = null) :
    BaseViewState<List<Note>?>(notes, error)