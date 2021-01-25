package ru.geekbrains.easynotes.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ru.geekbrains.easynotes.databinding.ActivityMainBinding
import ru.geekbrains.easynotes.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    lateinit var ui: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)

        setSupportActionBar(ui.toolbar)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        adapter = MainAdapter()
        ui.recyclerNotes.adapter = adapter

        viewModel.viewState().observe(
            this,
            { state -> state?.let { adapter.notes = state.notes } }
        )
    }
}