package ru.geekbrains.easynotes.model

class Repository(private val remoteDataProvider: RemoteDataProvider) {

    fun getNotes() = remoteDataProvider.subscribeToAllNotes()

    fun saveNote(note: Note) = remoteDataProvider.saveNote(note)

    fun getNoteById(id: String) = remoteDataProvider.getNoteById(id)

    fun getCurrentUser() = remoteDataProvider.getCurrenUser()

    fun deleteNote(id: String) = remoteDataProvider.deleteNote(id)
}