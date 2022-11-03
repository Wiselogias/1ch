package com.wiselogia.a1ch.db

import com.wiselogia.a1ch.Message
import com.wiselogia.a1ch.models.UsefulMessageModel

class Provider(val data: MessagesRoom) {
    fun updateMessages(update: List<Message>) = data.messagesDao().addAll(update.map {
        UsefulMessageModel(
            it.id,
            it.from,
            it.time,
            it.data.Image?.link,
            it.data.Text?.text,
        )
    })

    fun getMessages() = data.messagesDao().getAll()
}
