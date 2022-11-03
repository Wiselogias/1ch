package com.wiselogia.a1ch.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class UsefulMessageModel(
    @PrimaryKey var id: Int = 0,
    @ColumnInfo(name = "from") var from: String = "",
    @ColumnInfo(name = "time") var time: Long = 0L,
    @ColumnInfo(name = "image") var image: String? = null,
    @ColumnInfo(name = "text") var text: String? = null
)