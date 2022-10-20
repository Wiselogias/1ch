package com.wiselogia.a1ch

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class SendNRecieveService : Service() {
    private lateinit var fetcher: Fetcher

    override fun onBind(p0: Intent?) = null

    override fun onCreate() {
        super.onCreate()
        Retrofit.networkExecutor.execute {
            fetcher = Fetcher { (application as App).messagesProvider.updateMessages(it) }
            fetcher.run()
        }
    }

    inner class MyBinder : Binder() {
        fun getService() = this@SendNRecieveService
    }
}