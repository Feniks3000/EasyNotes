package ru.geekbrains.easynotes.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.geekbrains.easynotes.R
import ru.geekbrains.easynotes.databinding.ItemNoteBinding
import ru.geekbrains.easynotes.model.Note
import ru.geekbrains.easynotes.ui.getColorInt

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
            note.run {
                ui.title.text = title
                ui.body.text = body
                ui.noteCard.setCardBackgroundColor(color.getColorInt(itemView.context))
            }
            itemView.setOnClickListener { onItemClickListener.onItemClick(note) }
        }
    }
}

interface OnItemClickListener {
    fun onItemClick(note: Note)
}