package ru.geekbrains.easynotes.model

import java.util.*

object Repository {

    private val remoteDataProvider: RemoteDataProvider = FirebaseStoreProvider()

    fun getNotes() = remoteDataProvider.subscribeToAllNotes()

    fun saveNote(note: Note) = remoteDataProvider.saveNote(note)

    fun getNoteById(id: String) = remoteDataProvider.getNoteById(id)

    fun getNewId() = UUID.randomUUID().toString()

    fun getCurrentUser() = remoteDataProvider.getCurrenUser()
}