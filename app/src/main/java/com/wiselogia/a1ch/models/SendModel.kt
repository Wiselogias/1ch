package com.wiselogia.a1ch.models

import com.squareup.moshi.Json
import com.wiselogia.a1ch.Data

data class SendModel(
    @Json(name = "from")
    val from : String,
    @Json(name = "data")
    val data: Data
)
