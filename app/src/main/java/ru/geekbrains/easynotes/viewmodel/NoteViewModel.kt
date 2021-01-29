package ru.geekbrains.easynotes.viewmodel

import androidx.lifecycle.ViewModel
import org.apache.commons.lang3.StringUtils
import ru.geekbrains.easynotes.model.Note
import ru.geekbrains.easynotes.model.Repository

class NoteViewModel(private val repository: Repository = Repository) : ViewModel() {

    private var pendingNote: Note? = null

    fun saveChanges(note: Note) {
        pendingNote = note
    }

    override fun onCleared() {
        if (pendingNote != null) {
            if (StringUtils.isEmpty(pendingNote!!.title)) pendingNote =
                pendingNote!!.copy(title = "New note")
            repository.saveNote(pendingNote!!)
        }
    }
}