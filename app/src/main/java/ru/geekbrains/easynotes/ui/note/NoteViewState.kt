package ru.geekbrains.easynotes.ui.note

import ru.geekbrains.easynotes.model.Note
import ru.geekbrains.easynotes.ui.base.BaseViewState

class NoteViewState(data: Data = Data(), error: Throwable? = null) :
    BaseViewState<NoteViewState.Data>(data, error) {

    data class Data(val isDeleted: Boolean = false, val note: Note? = null)
}