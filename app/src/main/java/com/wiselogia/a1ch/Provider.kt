package com.wiselogia.a1ch

import android.os.Handler
import android.os.Looper
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class Provider(val data: File) {
    var messages = mutableListOf<UsefulMessageModel>()
    private val ioExecutor = Executors.newSingleThreadExecutor()
    private val callbacks = mutableListOf<OnDataChangeListener>()
    private val handler = Handler(Looper.getMainLooper())
    fun registerDataChangeListener(callback: OnDataChangeListener) {
        callbacks.add(callback)
    }

    fun unregisterCallback(callback: OnDataChangeListener) {
        callbacks.remove(callback)
    }

    fun updateMessages(update: List<Message>) {
        
        messages += update.map {
            UsefulMessageModel(
                it.from,
                it.time,
                it.data.image?.link,
                it.data.text?.text
            )
        }.toMutableList()
        messages = messages.distinct().toMutableList()
        var jsonObj = messages.map {
            it.serialize()
        }
        val writer = FileWriter(data, false)
        writer.write(jsonObj.toString())
        writer.close()
        handler.post {
            callbacks.forEach {
                it.onChange(messages)
            }
        }
        getMessages()

    }

    fun getMessages() {
        ioExecutor.run {
            val reader = data.bufferedReader().use { it.readText() }
            val converted = JSONArray(reader)
            messages = mutableListOf()
            for (i in 0 until converted.length()) {
                messages.add(UsefulMessageModel().apply { deserialize(converted.getJSONObject(i)) })
            }
            handler.post {
                callbacks.forEach {
                    it.onChange(messages)
                }
            }
        }
    }
}

interface OnDataChangeListener {
    fun onChange(data: List<UsefulMessageModel>)
}