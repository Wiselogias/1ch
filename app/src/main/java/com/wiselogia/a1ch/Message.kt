package com.wiselogia.a1ch

import org.json.JSONObject

data class Message(
    var id: Int = 0,
    var from: String = "",
    var time: Long = 0L,
    var data: Data = Data()
) : Serializable, Deserialize {
    override fun serialize() = JSONObject().apply {
        put("from", from)
        put("time", time)
        put("data", data.serialize())
    }

    override fun deserialize(json: JSONObject) {
        id = json.getInt("id")
        from = json.getString("from")
        time = json.getLong("time")
        data = Data().apply {
            deserialize(json.getJSONObject("data"))
        }
    }
}


data class Data(
    var image: Image? = null,
    var text: Text? = null
) : Serializable, Deserialize {
    override fun serialize() = JSONObject().apply {
        if (image != null)
            put("Image", image!!.serialize())
        else
            put("Text", text?.serialize() ?: "")
    }

    override fun deserialize(json: JSONObject) {
        if (json.has("Image"))
            image = Image().apply { deserialize(json.getJSONObject("Image")) }
        if(json.has("Text"))
            text = Text().apply { deserialize(json.getJSONObject("Text")) }
    }
}


data class Image(
    var link: String = ""
) : Serializable, Deserialize {
    override fun serialize() = JSONObject().apply {
        put("link", link)
    }

    override fun deserialize(json: JSONObject) {
        link = json.getString("link")
    }
}


data class Text(
    var text: String = ""
) : Serializable, Deserialize {
    override fun serialize() = JSONObject().apply {
        put("text", text)
    }

    override fun deserialize(json: JSONObject) {
        text = json.getString("text")
    }
}

interface Serializable {
    fun serialize(): JSONObject
}

interface Deserialize {
    fun deserialize(json: JSONObject)
}