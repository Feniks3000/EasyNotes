package ru.geekbrains.easynotes.viewmodel

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import ru.geekbrains.easynotes.model.Note
import ru.geekbrains.easynotes.model.Repository
import ru.geekbrains.easynotes.ui.note.NoteViewState

class NoteViewModel(val repository: Repository) :
    BaseViewModel<NoteViewState.Data>() {

    private val currentNote: Note?
        get() = getViewState().poll()?.note

    fun saveChanges(note: Note) {
        setData(NoteViewState.Data(note = note))
    }

    fun loadNote(id: String) {
        launch {
            try {
                setData(NoteViewState.Data(note = repository.getNoteById(id)))
            } catch (e: Throwable) {
                setError(e)
            }
        }
    }

    fun deleteNote() {
        launch {
            try {
                currentNote?.let { repository.deleteNote(it.id) }
                setData(NoteViewState.Data(isDeleted = true))
            } catch (e: Throwable) {
                setError(e)
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onCleared() {
        launch {
            currentNote?.let {
                if (StringUtils.isEmpty(it.title)) {
                    repository.saveNote(it.copy(title = "New note"))
                } else {
                    repository.saveNote(it)
                }
            }
        }
    }
}