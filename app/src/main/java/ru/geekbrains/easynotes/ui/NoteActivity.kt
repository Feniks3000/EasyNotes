package ru.geekbrains.easynotes.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ru.geekbrains.easynotes.databinding.ActivityMainBinding
import ru.geekbrains.easynotes.databinding.ActivityNoteBinding
import ru.geekbrains.easynotes.model.Note
import ru.geekbrains.easynotes.model.Repository
import ru.geekbrains.easynotes.model.getColorInt
import ru.geekbrains.easynotes.viewmodel.MainViewModel
import ru.geekbrains.easynotes.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.*

private val SAVE_DELAY = 2000L

class NoteActivity : BaseActivity<Note?, NoteViewState>() {

    companion object {
        private val EXTRA_NOTE = NoteActivity::class.java.name + "extra.NOTE"

        fun getStartIntent(context: Context, noteId: String?): Intent =
            Intent(context, NoteActivity::class.java).apply {
                putExtra(EXTRA_NOTE, noteId)
            }
    }

    private var note: Note? = null
    override val ui: ActivityNoteBinding by lazy { ActivityNoteBinding.inflate(layoutInflater) }
    override val viewModel: NoteViewModel by lazy { ViewModelProvider(this).get(NoteViewModel::class.java) }
    private val textChangeListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            triggerSaveNote()
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val noteId = intent.getStringExtra(EXTRA_NOTE)

        setSupportActionBar(ui.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (note != null) {
            SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault()).format(note!!.lastChanged)
        } else {
            SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault()).format(Date())
        }

        noteId?.let {
            viewModel.loadNote(noteId)
        }

        initView()
    }

    private fun initView() {
        ui.title.setText(note?.title ?: "")
        ui.body.setText(note?.body ?: "")
        ui.toolbar.setBackgroundResource(getColorInt(note?.color))

        ui.title.addTextChangedListener(textChangeListener)
        ui.body.addTextChangedListener(textChangeListener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun triggerSaveNote() {
        if (ui.title.text == null || ui.body.text == null) return

        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
            override fun run() {
                note = note?.copy(
                    title = ui.title.text.toString(),
                    body = ui.body.text.toString(),
                    lastChanged = Date()
                ) ?: createNewNote()

                if (note != null) viewModel.saveChanges(note!!)
            }
        }, SAVE_DELAY)
    }

    private fun createNewNote(): Note =
        Note(
            Repository.getNewId(),
            ui.title.text.toString(),
            ui.body.text.toString()
        )

    override fun renderData(data: Note?) {
        this.note = data
        initView()
    }
}