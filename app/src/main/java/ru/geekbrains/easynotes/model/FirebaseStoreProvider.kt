package ru.geekbrains.easynotes.model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import ru.geekbrains.easynotes.exceptions.NoAuthException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

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

    override suspend fun subscribeToAllNotes(): ReceiveChannel<NoteResult> =
        Channel<NoteResult>(Channel.CONFLATED).apply {
            var registration: ListenerRegistration? = null
            try {
                registration = getUserNotes()
                    .addSnapshotListener { snapshot, exception ->
                        val value = exception?.let { NoteResult.Error(exception) }
                            ?: snapshot?.let {
                                val notes = snapshot.documents.map { document ->
                                    document.toObject(Note::class.java)
                                }
                                NoteResult.Success(notes)
                            }
                        value?.let { offer(it) }
                    }
            } catch (e: Throwable) {
                offer(NoteResult.Error(e))
            }
            invokeOnClose { registration?.remove() }
        }

    override suspend fun getNoteById(id: String): Note = suspendCoroutine { continuation ->
        try {
            getUserNotes().document(id).get()
                .addOnSuccessListener { snapshot ->
                    continuation.resume(snapshot.toObject(Note::class.java)!!)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        } catch (e: Throwable) {
            continuation.resumeWithException(e)
        }
    }

    override suspend fun saveNote(note: Note): Note = suspendCoroutine { continuation ->
        try {
            getUserNotes().document(note.id).set(note)
                .addOnSuccessListener {
                    Log.d(CLASS, "Note $note is saved")
                    continuation.resume(note)
                }
                .addOnFailureListener { exception ->
                    Log.d(CLASS, "Error saving note $note, message: ${exception.message}")
                    continuation.resumeWithException(exception)
                }
        } catch (e: Throwable) {
            continuation.resumeWithException(e)
        }
    }

    override suspend fun getCurrenUser(): User = suspendCoroutine { continuation ->
        currentUser?.let {
            continuation.resume(User(it.displayName ?: "", it.email ?: ""))
        } ?: continuation.resumeWithException(NoAuthException())
    }

    override suspend fun deleteNote(id: String): Note? = suspendCoroutine { continuation ->
        try {
            getUserNotes()
                .document(id)
                .delete()
                .addOnSuccessListener { continuation.resume(null) }
                .addOnFailureListener { continuation.resumeWithException(it) }
        } catch (e: Throwable) {
            continuation.resumeWithException(e)
        }
    }

    private fun getUserNotes() = currentUser?.let { firebaseUser ->
        db.collection(USERS_COLLECTION)
            .document(firebaseUser.uid)
            .collection(NOTES_COLLECTION)
    } ?: throw NoAuthException()
}