package ru.geekbrains.easynotes.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.geekbrains.easynotes.R
import ru.geekbrains.easynotes.databinding.ItemNoteBinding
import ru.geekbrains.easynotes.model.Note
import ru.geekbrains.easynotes.model.getColorInt

class MainAdapter(private val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<MainAdapter.NoteViewHolder>() {

    var notes: List<Note> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder =
        NoteViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        )

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) =
        holder.bind(notes[position])

    override fun getItemCount(): Int = notes.size

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ui: ItemNoteBinding = ItemNoteBinding.bind(itemView)

        fun bind(note: Note) {
            ui.title.text = note.title
            ui.body.text = note.body
            ui.body.setBackgroundResource(getColorInt(note.color))
            itemView.setBackgroundColor(getColorInt(note.color))
            itemView.setOnClickListener { onItemClickListener.onItemClick(note) }
        }
    }
}

interface OnItemClickListener {
    fun onItemClick(note: Note)
}