package ru.geekbrains.easynotes.viewmodel

import androidx.lifecycle.Observer
import ru.geekbrains.easynotes.model.Note
import ru.geekbrains.easynotes.model.NoteResult
import ru.geekbrains.easynotes.model.Repository
import ru.geekbrains.easynotes.ui.MainViewState

class MainViewModel(repository: Repository) :
    BaseViewModel<List<Note>?, MainViewState>() {

    private val repositoryNotes = repository.getNotes()

    private val notesObserver = object : Observer<NoteResult> {
        override fun onChanged(t: NoteResult?) {
            t?.let {
                when (t) {
                    is NoteResult.Success<*> -> {
                        viewStateLiveData.value = MainViewState(notes = t.data as? List<Note>)
                    }
                    is NoteResult.Error -> {
                        viewStateLiveData.value = MainViewState(error = t.error)
                    }
                }
            }
        }
    }

    init {
        viewStateLiveData.value = MainViewState()
        repositoryNotes.observeForever(notesObserver)
    }

    override fun onCleared() {
        repositoryNotes.removeObserver(notesObserver)
    }
}