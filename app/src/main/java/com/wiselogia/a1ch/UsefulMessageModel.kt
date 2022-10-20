package com.wiselogia.a1ch

import org.json.JSONObject

data class UsefulMessageModel(
    var from: String = "",
    var time: Long = 0L,
    var image: String? = null,
    var text: String? = null
) : Serializable, Deserialize {
    override fun serialize() = JSONObject().apply {
        put("from", from)
        put("time", time)
        put("image", image)
        put("text", text)
    }

    override fun deserialize(json: JSONObject) {
        from = json.getString("from")
        time = json.getLong("time")
        if(json.has("image"))
            image = json.getString("image")
        if(json.has("text"))
            text = json.getString("text")
    }
}