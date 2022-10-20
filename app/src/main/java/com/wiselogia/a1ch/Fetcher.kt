package com.wiselogia.a1ch

import org.json.JSONArray
import org.json.JSONObject

class Fetcher(private val onChange: (List<Message>) -> Unit) : Thread() {
    var isRunning = true
    var lastKnown = 0
    override fun run() {
        while (isRunning) {
            val data = Retrofit.sendRequest(
                url = "/1ch?limit=1000&lastKnownId=${lastKnown}",
                method = "GET",
                data = null
            )

            val jsonArray = JSONArray(data)
            val messages = mutableListOf<Message>()
            for (i in 0 until jsonArray.length()) {
                messages.add(Message().apply { deserialize(jsonArray.getJSONObject(i)) })
            }
            setLast(lastKnown + messages.size)
            onChange(messages)
            sleep(3000L)
        }
    }

    fun setLast(last: Int) {
        lastKnown = last
    }

}