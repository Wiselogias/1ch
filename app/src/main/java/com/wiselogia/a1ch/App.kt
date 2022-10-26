package com.wiselogia.a1ch

import android.app.Application
import androidx.room.Room
import com.wiselogia.a1ch.db.MessagesRoom
import com.wiselogia.a1ch.db.Provider
import com.wiselogia.a1ch.network.Api
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class App : Application() {
    private val retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl("http://213.189.221.170:8008")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
    val api by lazy { retrofit.create(Api::class.java) }
    val messagesProvider by lazy {
        Provider(messagesRoom)
    }
    private val messagesRoom by lazy {
        Room.databaseBuilder(this, MessagesRoom::class.java, "database").allowMainThreadQueries().build()
    }
}