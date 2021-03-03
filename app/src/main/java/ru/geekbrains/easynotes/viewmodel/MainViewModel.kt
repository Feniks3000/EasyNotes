package ru.geekbrains.easynotes.viewmodel

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.geekbrains.easynotes.model.Note
import ru.geekbrains.easynotes.model.NoteResult
import ru.geekbrains.easynotes.model.Repository

class MainViewModel(repository: Repository) :
    BaseViewModel<List<Note>?>() {

    private val notesChannel by lazy { runBlocking { repository.getNotes() } }

    init {
        launch {
            notesChannel.consumeEach { result ->
                when (result) {
                    is NoteResult.Success<*> -> setData(result.data as? List<Note>)
                    is NoteResult.Error -> setError(result.error)
                }
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onCleared() {
        notesChannel.cancel()
    }
}