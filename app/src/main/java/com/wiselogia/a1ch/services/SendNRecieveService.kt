package com.wiselogia.a1ch.services

import android.app.Service
import android.content.Intent
import com.wiselogia.a1ch.App
import kotlinx.coroutines.*

class SendNRecieveService : Service() {
    var lastKnown = 0
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }
    @OptIn(DelicateCoroutinesApi::class)
    private val job = GlobalScope.launch(coroutineExceptionHandler, start = CoroutineStart.LAZY) {
        while (true) {
            val data = (application as App).api.getMessages(1000, 0)
            (application as App).messagesProvider.updateMessages(data)
            setLast(lastKnown + data.size)
            delay(3000L)
        }
    }
    override fun onBind(p0: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        job.start()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    fun setLast(last: Int) {
        lastKnown = last
    }
}