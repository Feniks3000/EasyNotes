package ru.geekbrains.easynotes.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ru.geekbrains.easynotes.exceptions.NoAuthException

private const val NOTES_COLLECTION = "notes"
private const val USERS_COLLECTION = "users"

class FirebaseStoreProvider(
    private val db: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : RemoteDataProvider {
    companion object {
        private val CLASS = "${FirebaseStoreProvider::class.java.simpleName}"
    }

    private val currentUser
        get() = firebaseAuth.currentUser

    override fun subscribeToAllNotes(): LiveData<NoteResult> = MutableLiveData<NoteResult>().apply {
        try {
            getUserNotes().addSnapshotListener { snapshot, exception ->
                value = exception?.let { NoteResult.Error(exception) }
                    ?: snapshot?.let { querySnapshot ->
                        val notes =
                            querySnapshot.documents.map { doc -> doc.toObject(Note::class.java) }
                        NoteResult.Success(notes)
                    }
            }
        } catch (e: Throwable) {
            value = NoteResult.Error(e)
        }
    }

    override fun getNoteById(id: String): LiveData<NoteResult> =
        MutableLiveData<NoteResult>().apply {
            try {
                getUserNotes().document(id).get()
                    .addOnSuccessListener { snapshot ->
                        value = NoteResult.Success(snapshot.toObject(Note::class.java))
                    }
                    .addOnFailureListener { exception ->
                        value = NoteResult.Error(exception)
                    }
            } catch (e: Throwable) {
                value = NoteResult.Error(e)
            }
        }

    override fun saveNote(note: Note): LiveData<NoteResult> = MutableLiveData<NoteResult>().apply {
        try {
            getUserNotes().document(note.id).set(note)
                .addOnSuccessListener {
                    Log.d(CLASS, "Note $note is saved")
                    value = NoteResult.Success(note)
                }
                .addOnFailureListener { exception ->
                    Log.d(CLASS, "Error saving note $note, message: ${exception.message}")
                    value = NoteResult.Error(exception)
                }
        } catch (e: Throwable) {
            value = NoteResult.Error(e)
        }
    }

    override fun getCurrenUser(): LiveData<User?> = MutableLiveData<User?>().apply {
        value = currentUser?.let {
            User(it.displayName ?: "", it.email ?: "")
        }
    }

    override fun deleteNote(id: String): LiveData<NoteResult> =
        MutableLiveData<NoteResult>().apply {
            try {
                getUserNotes()
                    .document(id)
                    .delete()
                    .addOnSuccessListener { value = NoteResult.Success(null) }
                    .addOnFailureListener { throw it }
            } catch (e: Throwable) {
                value = NoteResult.Error(e)
            }
        }

    private fun getUserNotes() = currentUser?.let { firebaseUser ->
        db.collection(USERS_COLLECTION)
            .document(firebaseUser.uid)
            .collection(NOTES_COLLECTION)
    } ?: throw NoAuthException()
}