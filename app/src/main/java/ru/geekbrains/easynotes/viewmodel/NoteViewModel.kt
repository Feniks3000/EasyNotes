package ru.geekbrains.easynotes.viewmodel

import androidx.lifecycle.Observer
import org.apache.commons.lang3.StringUtils
import ru.geekbrains.easynotes.model.Note
import ru.geekbrains.easynotes.model.NoteResult
import ru.geekbrains.easynotes.model.Repository
import ru.geekbrains.easynotes.ui.NoteViewState

class NoteViewModel(val repository: Repository = Repository) :
    BaseViewModel<NoteViewState.Data, NoteViewState>() {

    private var currentNote: Note? = null
        get() = viewStateLiveData.value?.data?.note

    fun saveChanges(note: Note) {
        viewStateLiveData.value = NoteViewState(NoteViewState.Data(note = note))
    }

    override fun onCleared() {
        currentNote?.let {
            if (StringUtils.isEmpty(it.title)) currentNote = it.copy(title = "New note")
            repository.saveNote(currentNote!!)
        }
    }

    fun loadNote(id: String) =
        repository.getNoteById(id).observeForever(Observer { t ->
            t?.let { noteResult ->
                viewStateLiveData.value = when (noteResult) {
                    is NoteResult.Success<*> -> NoteViewState(NoteViewState.Data(note = noteResult.data as? Note))
                    is NoteResult.Error -> NoteViewState(error = noteResult.error)
                }
            }
        })

    fun deleteNote() {
        currentNote?.let {
            repository.deleteNote(it.id).observeForever { result ->
                result?.let { noteResult ->
                    viewStateLiveData.value = when (noteResult) {
                        is NoteResult.Success<*> -> NoteViewState(NoteViewState.Data(isDeleted = true))
                        is NoteResult.Error -> NoteViewState(error = noteResult.error)
                    }
                }
            }
        }

    }
}