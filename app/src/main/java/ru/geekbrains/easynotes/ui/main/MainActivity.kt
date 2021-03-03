package ru.geekbrains.easynotes.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.firebase.ui.auth.AuthUI
import org.koin.android.viewmodel.ext.android.viewModel
import ru.geekbrains.easynotes.R
import ru.geekbrains.easynotes.databinding.ActivityMainBinding
import ru.geekbrains.easynotes.model.Note
import ru.geekbrains.easynotes.ui.LogoutDialog
import ru.geekbrains.easynotes.ui.base.BaseActivity
import ru.geekbrains.easynotes.ui.note.NoteActivity
import ru.geekbrains.easynotes.ui.splash.SplashActivity
import ru.geekbrains.easynotes.viewmodel.MainViewModel

class MainActivity : BaseActivity<List<Note>?>(), LogoutDialog.LogoutListener {

    private val CLASS = MainActivity::class.java.simpleName

    override val ui: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override val viewModel: MainViewModel by viewModel()

    lateinit var adapter: MainAdapter

    companion object {
        fun getStartIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(ui.toolbar)

        adapter = MainAdapter(object : OnItemClickListener {
            override fun onItemClick(note: Note) {
                openNoteScreen(note)
            }
        })
        ui.recyclerNotes.adapter = adapter

        ui.floatingAddNoteButton.setOnClickListener { openNoteScreen(null) }
    }

    private fun openNoteScreen(note: Note?) =
        startActivity(NoteActivity.getStartIntent(this, note?.id))

    override fun renderData(data: List<Note>?) {
        if (data == null) return
        adapter.notes = data
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean =
        MenuInflater(this).inflate(R.menu.menu_main, menu).let { true }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.logout -> showLogoutDialog().let { true }
            else -> false
        }

    private fun showLogoutDialog() {
        supportFragmentManager.findFragmentByTag(LogoutDialog.CLASS)
        LogoutDialog.createInstance().show(supportFragmentManager, LogoutDialog.CLASS)
    }

    override fun onLogout() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                startActivity(Intent(this, SplashActivity::class.java))
                finish()
            }
    }
}