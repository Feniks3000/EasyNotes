package ru.geekbrains.easynotes.viewmodel

import androidx.lifecycle.Observer
import org.apache.commons.lang3.StringUtils
import ru.geekbrains.easynotes.model.Note
import ru.geekbrains.easynotes.model.NoteResult
import ru.geekbrains.easynotes.model.Repository
import ru.geekbrains.easynotes.ui.NoteViewState

class NoteViewModel(val repository: Repository = Repository) :
    BaseViewModel<Note?, NoteViewState>() {

    private var pendingNote: Note? = null

    fun saveChanges(note: Note) {
        pendingNote = note
    }

    override fun onCleared() {
        pendingNote?.let {
            if (StringUtils.isEmpty(it.title)) pendingNote = it.copy(title = "New note")
            repository.saveNote(pendingNote!!)
        }
    }

    fun loadNote(id: String) =
        repository.getNoteById(id).observeForever(object : Observer<NoteResult> {
            override fun onChanged(t: NoteResult?) {
                if (t == null) return

                when (t) {
                    is NoteResult.Success<*> ->
                        viewStateLiveData.value = NoteViewState(note = t.data as? Note)
                    is NoteResult.Error ->
                        viewStateLiveData.value = NoteViewState(error = t.error)
                }
            }
        })
}