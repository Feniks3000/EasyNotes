package ru.geekbrains.easynotes.ui

import ru.geekbrains.easynotes.model.Note

class NoteViewState(note: Note? = null, error: Throwable? = null) :
    BaseViewState<Note?>(note, error)