package ru.geekbrains.easynotes.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ru.geekbrains.easynotes.databinding.ActivityMainBinding
import ru.geekbrains.easynotes.model.Note
import ru.geekbrains.easynotes.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    lateinit var ui: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        setSupportActionBar(ui.toolbar)

        adapter = MainAdapter(object : OnItemClickListener {
            override fun onItemClick(note: Note) {
                openNoteScreen(note)
            }
        })
        ui.recyclerNotes.adapter = adapter

        viewModel.viewState().observe(
            this,
            { state -> state?.let { adapter.notes = state.notes } }
        )

        ui.floatingAddNoteButton.setOnClickListener { view ->
            openNoteScreen(null)
        }
    }

    private fun openNoteScreen(note: Note?) {
        startActivity(NoteActivity.getStartIntent(this, note))
    }
}