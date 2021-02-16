package ru.geekbrains.easynotes.model

import kotlinx.coroutines.channels.ReceiveChannel

interface RemoteDataProvider {
    suspend fun subscribeToAllNotes(): ReceiveChannel<NoteResult>

    suspend fun getNoteById(id: String): Note

    suspend fun saveNote(note: Note): Note

    suspend fun getCurrenUser(): User?

    suspend fun deleteNote(id: String): Note?
}