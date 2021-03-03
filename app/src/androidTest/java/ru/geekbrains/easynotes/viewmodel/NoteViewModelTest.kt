package ru.geekbrains.easynotes.viewmodel

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import io.mockk.*
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import ru.geekbrains.easynotes.R
import ru.geekbrains.easynotes.model.Color
import ru.geekbrains.easynotes.model.Note
import ru.geekbrains.easynotes.ui.getColorInt
import ru.geekbrains.easynotes.ui.note.NoteActivity
import ru.geekbrains.easynotes.ui.note.NoteViewState

class NoteViewModelTest {

    @get:Rule   // Правило для запуска Activity
    val activityTestRule = ActivityTestRule(NoteActivity::class.java, true, false)

    private val viewModel: NoteViewModel = spyk(NoteViewModel(mockk()))
    private val viewStateLiveData = MutableLiveData<NoteViewState>()
    private val testNote = Note("1", "title", "body")

    @Before
    fun setUp() {
        startKoin {
            modules()
        }

        loadKoinModules(
            module {
                viewModel { viewModel }
            }
        )

        every { viewModel.getViewState() } returns viewStateLiveData
        every { viewModel.loadNote(any()) } just runs
        every { viewModel.saveChanges(any()) } just runs
        every { viewModel.deleteNote() } just runs


        activityTestRule.launchActivity(Intent().apply {
            putExtra(NoteActivity::class.java.name + "extra.NOTE", testNote.id)
        })
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun should_show_color_picker() {
        onView(withId(R.id.palette)).perform(click())

        onView(withId(R.id.colorPicker)).check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun should_hide_color_picker() {
        onView(withId(R.id.palette)).perform(click()).perform(click())

        onView(withId(R.id.colorPicker)).check(matches(not(isDisplayed())))
    }

    @Test
    fun should_set_toolbar_color() {
        onView(withId(R.id.palette)).perform(click())
        onView(withTagValue(`is`(Color.BLUE))).perform(click())

        onView(withId(R.id.toolbar)).check { view, _ ->
            assertTrue(
                "toolbar background color does not match",
                (view.background as? ColorDrawable)?.color ==
                        Color.BLUE.getColorInt(activityTestRule.activity)
            )
        }
    }

    @Test
    fun should_call_viewModel_loadNote() {
        verify(exactly = 1) { viewModel.loadNote(testNote.id) }
    }

    @Test
    fun should_show_note() {
        activityTestRule.launchActivity(null)
        viewStateLiveData.postValue(NoteViewState(NoteViewState.Data(note = testNote)))

        onView(withId(R.id.title)).check(matches(withText(testNote.title)))
        onView(withId(R.id.body)).check(matches(withText(testNote.body)))
    }

    @Test
    fun should_call_saveNote() {
        onView(withId(R.id.title)).perform(typeText(testNote.title))
        verify(timeout = 2000) { viewModel.saveChanges(any()) }
    }

    @Test
    fun should_not_call_saveNote() {
        onView(withId(R.id.title)).perform(typeText(""))
        verify(timeout = 2000, exactly = 0) { viewModel.saveChanges(any()) }
    }

    @Test
    fun should_call_deleteNote() {
        openActionBarOverflowOrOptionsMenu(activityTestRule.activity)
        onView(withText(R.string.delete_menu_title)).perform(click())
        onView(withText(R.string.delete_dialog_ok)).perform(click())
        verify(exactly = 1) { viewModel.deleteNote() }
    }

}