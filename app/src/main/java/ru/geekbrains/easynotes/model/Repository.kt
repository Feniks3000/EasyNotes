package ru.geekbrains.easynotes.model

class Repository(private val remoteDataProvider: RemoteDataProvider) {

    suspend fun getNotes() = remoteDataProvider.subscribeToAllNotes()

    suspend fun saveNote(note: Note) = remoteDataProvider.saveNote(note)

    suspend fun getNoteById(id: String) = remoteDataProvider.getNoteById(id)

    suspend fun getCurrentUser() = remoteDataProvider.getCurrenUser()

    suspend fun deleteNote(id: String) = remoteDataProvider.deleteNote(id)
}