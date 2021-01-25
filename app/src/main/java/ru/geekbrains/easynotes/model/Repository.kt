package ru.geekbrains.easynotes.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.apache.commons.lang3.RandomStringUtils
import java.util.*

object Repository {
    private val notesLiveData = MutableLiveData<List<Note>>()

    private val notes: MutableList<Note> = mutableListOf(
        Note(getNewId(), "First note", RandomStringUtils.randomAlphabetic(50), Color.BLUE),
        Note(getNewId(), "Second note", RandomStringUtils.randomAlphabetic(50)),
        Note(getNewId(), "", RandomStringUtils.randomAlphabetic(50), Color.GRAY)
    )

    fun getNewId() = UUID.randomUUID().toString()

    init {
        notesLiveData.value = notes
    }

    fun getNotes(): LiveData<List<Note>> = notesLiveData

    fun saveNote(note: Note) {
        addOrReplace(note)
        notesLiveData.value = notes
    }

    fun addOrReplace(note: Note) {
        for (i in 0 until notes.size) {
            if (notes[i].id == note.id) {
                notes[i] = note
                return
            }
        }
        notes.add(note)
    }

}