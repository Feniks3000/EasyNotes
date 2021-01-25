package ru.geekbrains.easynotes.model

import android.graphics.Color
import org.apache.commons.lang3.StringUtils

class Note(title: String, body: String, color: Int = Color.WHITE) {
    val title: String
    val body: String
    val color: Int

    init {
        if (StringUtils.isNotEmpty(title)) {
            this.title = title
        } else {
            this.title = "New note"
        }
        this.body = body
        this.color = color
    }

}