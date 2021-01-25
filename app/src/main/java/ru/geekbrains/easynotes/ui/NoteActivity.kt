package ru.geekbrains.easynotes.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.geekbrains.easynotes.databinding.ActivityNoteBinding

class NoteActivity : AppCompatActivity() {

    lateinit var ui: ActivityNoteBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(ui.root)

        setSupportActionBar(ui.toolbar)

    }
}