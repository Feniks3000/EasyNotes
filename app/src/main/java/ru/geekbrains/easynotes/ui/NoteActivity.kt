package ru.geekbrains.easynotes.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import org.koin.android.viewmodel.ext.android.viewModel
import ru.geekbrains.easynotes.R
import ru.geekbrains.easynotes.databinding.ActivityNoteBinding
import ru.geekbrains.easynotes.model.Color
import ru.geekbrains.easynotes.model.Note
import ru.geekbrains.easynotes.viewmodel.NoteViewModel
import java.util.*

private val SAVE_DELAY = 2000L

class NoteActivity : BaseActivity<NoteViewState.Data, NoteViewState>() {

    companion object {
        private val EXTRA_NOTE = NoteActivity::class.java.name + "extra.NOTE"

        fun getStartIntent(context: Context, noteId: String?) =
            Intent(context, NoteActivity::class.java).apply {
                putExtra(EXTRA_NOTE, noteId)
            }
    }

    private var note: Note? = null
    private var color: Color = Color.WHITE
    override val ui: ActivityNoteBinding by lazy { ActivityNoteBinding.inflate(layoutInflater) }
    override val viewModel: NoteViewModel by viewModel()
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

        noteId?.let {
            viewModel.loadNote(noteId)
        }

        ui.colorPicker.onColorClickListener = {
            color = it
            setToolbarColor(it)
            triggerSaveNote()
        }
        setTextChangedListeners()
        //initView()
    }

    private fun initView() {
        note?.run {
            removeTextChangedListeners()
            if (!title.equals(ui.title.text.toString())) {
                ui.title.setText(title)
            }
            if (!body.equals(ui.body.text.toString())) {
                ui.body.setText(body)
            }
            setTextChangedListeners()
            supportActionBar?.title = lastChanged.format()
            setToolbarColor(color)
        }
    }

    private fun setToolbarColor(color: Color) {
        ui.toolbar.setBackgroundColor(color.getColorInt(this@NoteActivity))
    }

    private fun setTextChangedListeners() {
        ui.title.addTextChangedListener(textChangeListener)
        ui.body.addTextChangedListener(textChangeListener)
    }

    private fun removeTextChangedListeners() {
        ui.title.removeTextChangedListener(textChangeListener)
        ui.body.removeTextChangedListener(textChangeListener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> super.onBackPressed().let { true }
        R.id.palette -> togglePalette().let { true }
        R.id.delete -> deleteNote().let { true }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean =
        menuInflater.inflate(R.menu.menu_note, menu).let { true }

    private fun deleteNote() {
        AlertDialog.Builder(this)
            .setMessage(R.string.delete_dialog_message)
            .setNegativeButton(R.string.delete_dialog_cancel) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.delete_dialog_ok) { _, _ -> viewModel.deleteNote() }
            .show()
    }

    private fun togglePalette() {
        if (ui.colorPicker.isOpen) {
            ui.colorPicker.close()
        } else {
            ui.colorPicker.open()
        }
    }

    private fun triggerSaveNote() {
        if (ui.title.text == null || ui.body.text == null) return

        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
            override fun run() {
                note = note?.copy(
                    title = ui.title.text.toString(),
                    body = ui.body.text.toString(),
                    color = color,
                    lastChanged = Date()
                ) ?: createNewNote()

                if (note != null) viewModel.saveChanges(note!!)
            }
        }, SAVE_DELAY)
    }

    private fun createNewNote(): Note =
        Note(
            UUID.randomUUID().toString(),
            ui.title.text.toString(),
            ui.body.text.toString()
        )

    override fun renderData(data: NoteViewState.Data) {
        if (data.isDeleted) finish()

        this.note = data.note
        data.note?.let { color = it.color }
        initView()
    }

    override fun onBackPressed() {
        if (ui.colorPicker.isOpen) {
            ui.colorPicker.close()
            return
        }
        super.onBackPressed()
    }
}