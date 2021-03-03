package ru.geekbrains.easynotes.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.geekbrains.easynotes.model.Note
import ru.geekbrains.easynotes.model.NoteResult
import ru.geekbrains.easynotes.model.Repository
import ru.geekbrains.easynotes.ui.note.NoteViewState

class NoteViewModelUnitTest {

    @get:Rule   // Правило для использования LiveData
    val taskExecuteRule = InstantTaskExecutorRule()

    private val mockRepository = mockk<Repository>()
    private val noteLiveData = MutableLiveData<NoteResult>()
    private lateinit var viewModel: NoteViewModel

    private val testNote = Note("1", "title", "body")

    @Before
    fun setUp() {
        every { mockRepository.getNoteById(any()) } returns noteLiveData
        every { mockRepository.saveNote(any()) } returns noteLiveData
        every { mockRepository.deleteNote(any()) } returns noteLiveData

        viewModel = NoteViewModel(mockRepository)

        viewModel.saveChanges(testNote) // инициализация currentNote, чтобы loadNote и deleteNote вернули результат и дернули репозиторий
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `should call getNoteById once for loadNote`() {
        viewModel.loadNote(testNote.id)
        verify(exactly = 1) { mockRepository.getNoteById(any()) }
    }

    @Test
    fun `should loadNote return error`() {
        var result: Throwable? = null
        val testData = Throwable("error")

        viewModel.getViewState().observeForever { result = it?.error }
        noteLiveData.value = NoteResult.Error(testData)
        viewModel.loadNote(testNote.id)

        assertEquals(result, testData)
    }

    @Test
    fun `should loadNote return success`() {
        var result: NoteViewState.Data? = null

        viewModel.getViewState().observeForever { result = it?.data }
        noteLiveData.value = NoteResult.Success(testNote)

        assertEquals(result!!.note, testNote)
    }

    @Test
    fun `should not call saveNote for saveChanges`() {
        viewModel.saveChanges(testNote)
        verify(exactly = 0) { mockRepository.saveNote(any()) }
    }

    @Test
    fun `should call saveNote once for onCleared`() {
        viewModel.saveChanges(testNote)
        viewModel.onCleared()
        verify(exactly = 1) { mockRepository.saveNote(any()) }
    }

    @Test
    fun `should deleteNote return error`() {
        var result: Throwable? = null
        val testData = Throwable("error")

        viewModel.getViewState().observeForever { result = it?.error }
        noteLiveData.value = NoteResult.Error(testData)
        viewModel.deleteNote()

        assertEquals(result, testData)
    }

    @Test
    fun `should deleteNote return success`() {
        var result: NoteViewState.Data? = null

        viewModel.getViewState().observeForever { result = it?.data }
        noteLiveData.value = NoteResult.Success(testNote)
        viewModel.deleteNote()

        assertTrue(result!!.isDeleted)
    }


}