package ru.geekbrains.easynotes.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import ru.geekbrains.easynotes.databinding.ActivityMainBinding
import ru.geekbrains.easynotes.model.Note
import ru.geekbrains.easynotes.viewmodel.MainViewModel

class MainActivity : BaseActivity<List<Note>?, MainViewState>() {

    override val ui: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override val viewModel: MainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }

    lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(ui.toolbar)

        adapter = MainAdapter(object : OnItemClickListener {
            override fun onItemClick(note: Note) {
                openNoteScreen(note)
            }
        })
        ui.recyclerNotes.adapter = adapter

        ui.floatingAddNoteButton.setOnClickListener { view ->
            openNoteScreen(null)
        }
    }

    private fun openNoteScreen(note: Note?) =
        startActivity(NoteActivity.getStartIntent(this, note?.id))

    override fun renderData(data: List<Note>?) {
        if (data == null) return
        adapter.notes = data
    }
}