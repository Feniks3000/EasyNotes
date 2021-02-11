package ru.geekbrains.easynotes.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import io.mockk.*
import junit.framework.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.geekbrains.easynotes.exceptions.NoAuthException
import java.io.PrintWriter

class FirebaseStoreProviderTest {

    @get:Rule   // Правило для использования LiveData
    val taskExecuteRule = InstantTaskExecutorRule()

    private val mockDb = mockk<FirebaseFirestore>()
    private val mockAuth = mockk<FirebaseAuth>()
    private val mockCollection = mockk<CollectionReference>()
    private val mockUser = mockk<FirebaseUser>()
    private val mockDocument1 = mockk<DocumentSnapshot>()
    private val mockDocument2 = mockk<DocumentSnapshot>()
    private val mockDocument3 = mockk<DocumentSnapshot>()
    private val testNotes = listOf(Note(id = "1"), Note(id = "2"), Note(id = "3"))

    private val provider: FirebaseStoreProvider = FirebaseStoreProvider(mockDb, mockAuth)

    @Before
    fun setUp() {
        clearMocks(mockCollection, mockDocument1, mockDocument2, mockDocument3)

        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns ""
        every { mockDb.collection(any()).document(any()).collection(any()) } returns mockCollection
        every { mockDocument1.toObject(Note::class.java) } returns testNotes[0]
        every { mockDocument2.toObject(Note::class.java) } returns testNotes[1]
        every { mockDocument3.toObject(Note::class.java) } returns testNotes[2]
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `should throw if no auth`() {
        var result: Any? = null

        every { mockAuth.currentUser } returns null
        provider.subscribeToAllNotes().observeForever {
            result = (it as? NoteResult.Error)?.error
        }

        assertTrue(result is NoAuthException)
    }

    @Test
    fun `subscribeAllNotes return Notes`() {
        var result: List<Note>? = null
        val slot = slot<EventListener<QuerySnapshot>>()
        val mockSnapshot = mockk<QuerySnapshot>()

        every { mockSnapshot.documents } returns listOf(mockDocument1, mockDocument2, mockDocument3)
        every { mockCollection.addSnapshotListener(capture(slot)) } returns mockk()

        provider.subscribeToAllNotes().observeForever {
            result = (it as? NoteResult.Success<List<Note>>)?.data
        }

        slot.captured.onEvent(mockSnapshot, null)

        assertEquals(result, testNotes)
    }

    @Test
    fun `subscribeAllNotes return error`() {
        var result: Throwable? = null
        val slot = slot<EventListener<QuerySnapshot>>()
        val testError = mockk<FirebaseFirestoreException>()

        every { mockCollection.addSnapshotListener(capture(slot)) } returns mockk()
        every { testError.printStackTrace(any<PrintWriter>()) } returns Unit
        //every { testError.message } returns ""

        provider.subscribeToAllNotes().observeForever {
            result = (it as? NoteResult.Error)?.error
        }

        slot.captured.onEvent(null, testError)

        assertNotNull(result)
        assertEquals(result, testError)
    }

    @Test
    fun `saveNote calls document set`() {
        val mockDocumentReference: DocumentReference = mockk()

        every { mockCollection.document(testNotes[0].id) } returns mockDocumentReference

        provider.saveNote(testNotes[0])

        verify(exactly = 1) { mockDocumentReference.set(testNotes[0]) }
    }

    @Test
    fun `saveNote return Note`() {
        var result: Note? = null
        val mockDocumentReference: DocumentReference = mockk()
        val slot = slot<OnSuccessListener<in Void>>()

        every { mockCollection.document(testNotes[0].id) } returns mockDocumentReference
        every {
            mockDocumentReference.set(testNotes[0]).addOnSuccessListener(capture(slot))
        } returns mockk()

        provider.saveNote(testNotes[0]).observeForever {
            result = (it as? NoteResult.Success<Note>)?.data
        }
        slot.captured.onSuccess(null)

        assertNotNull(result)
        assertEquals(result, testNotes[0])
    }
}