package ru.geekbrains.easynotes.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*

private const val NOTES_COLLECTION = "notes"

class FirebaseStoreProvider : RemoteDataProvider {
    companion object {
        private val CLASS = "${FirebaseStoreProvider::class.java.simpleName}"
    }

    private val db = FirebaseFirestore.getInstance()
    private val notesReference = db.collection(NOTES_COLLECTION)

    override fun subscribeToAllNotes(): LiveData<NoteResult> {
        val result = MutableLiveData<NoteResult>()

        notesReference.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(snapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    result.value = NoteResult.Error(error)
                } else if (snapshot != null) {
                    val notes = mutableListOf<Note>()
                    for (doc: QueryDocumentSnapshot in snapshot) {
                        notes.add(doc.toObject(Note::class.java))
                    }
                    result.value = NoteResult.Success(notes)
                }
            }

        })

        return result
    }

    override fun getNoteById(id: String): LiveData<NoteResult> {
        val result = MutableLiveData<NoteResult>()

        notesReference.document(id).get()
            .addOnSuccessListener { snapshot ->
                result.value = NoteResult.Success(snapshot.toObject(Note::class.java))
            }
            .addOnFailureListener { exception ->
                result.value = NoteResult.Error(exception)
            }

        return result
    }

    override fun saveNote(note: Note): LiveData<NoteResult> {
        val result = MutableLiveData<NoteResult>()

        notesReference.document(note.id).set(note)
            .addOnSuccessListener {
                Log.d(CLASS, "Note $note is saved")
                result.value = NoteResult.Success(note)
            }
            .addOnFailureListener { exception ->
                Log.d(CLASS, "Error saving note $note, message: ${exception.message}")
                result.value = NoteResult.Error(exception)
            }
        return result
    }
}