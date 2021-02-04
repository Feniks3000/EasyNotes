package ru.geekbrains.easynotes.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore

private const val NOTES_COLLECTION = "notes"

class FirebaseStoreProvider : RemoteDataProvider {
    companion object {
        private val CLASS = "${FirebaseStoreProvider::class.java.simpleName}"
    }

    private val db = FirebaseFirestore.getInstance()
    private val notesReference = db.collection(NOTES_COLLECTION)

    override fun subscribeToAllNotes(): LiveData<NoteResult> = MutableLiveData<NoteResult>().apply {
        notesReference.addSnapshotListener { snapshot, exception ->
            value = exception?.let { NoteResult.Error(exception) }
                ?: snapshot?.let { querySnapshot ->
                    val notes =
                        querySnapshot.documents.map { doc -> doc.toObject(Note::class.java) }
                    NoteResult.Success(notes)
                }
        }
    }

    override fun getNoteById(id: String): LiveData<NoteResult> =
        MutableLiveData<NoteResult>().apply {
            notesReference.document(id).get()
                .addOnSuccessListener { snapshot ->
                    value = NoteResult.Success(snapshot.toObject(Note::class.java))
                }
                .addOnFailureListener { exception ->
                    value = NoteResult.Error(exception)
                }
        }

    override fun saveNote(note: Note): LiveData<NoteResult> = MutableLiveData<NoteResult>().apply {
        notesReference.document(note.id).set(note)
            .addOnSuccessListener {
                Log.d(CLASS, "Note $note is saved")
                value = NoteResult.Success(note)
            }
            .addOnFailureListener { exception ->
                Log.d(CLASS, "Error saving note $note, message: ${exception.message}")
                value = NoteResult.Error(exception)
            }
    }
}