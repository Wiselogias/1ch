package com.wiselogia.a1ch

import com.squareup.moshi.Json


data class Message(
    @Json(name = "id")
    var id: Int = 0,
    @Json(name = "from")
    var from: String = "",
    @Json(name = "time")
    var time: Long = 0L,
    @Json(name = "data")
    var data: Data = Data()
)


data class Data(
    @Json(name = "Image")
    var Image: Image? = null,
    @Json(name = "Text")
    var Text: Text? = null
)


data class Image(
    @Json(name = "link")
    var link: String = ""
)


data class Text(
    @Json(name = "text")
    var text: String = ""
)

