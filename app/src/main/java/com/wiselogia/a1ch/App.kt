package com.wiselogia.a1ch

import android.app.Application
import java.io.File

class App : Application() {
    val messagesProvider by lazy {
        Provider(File(cacheDir, "myCache"))
    }
}