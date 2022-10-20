package com.wiselogia.a1ch

import android.util.Log
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

object Retrofit {
    val networkExecutor by lazy {
        Executors.newFixedThreadPool(10)
    }
    val DEFAULT_PATH = "http://213.189.221.170:8008"

    fun sendRequest(url: String, data: Serializable?, method: String = "POST") : String{
        val connection = URL(DEFAULT_PATH + url).openConnection() as HttpURLConnection
        connection.requestMethod = method
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Accept", "application/json")
        if (data != null)
            connection.outputStream.write(data.serialize().toString().toByteArray())


        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val res = connection.inputStream.bufferedReader()
                .use { it.readText() }
            return res
        } else {
            Log.e("HTTPURLCONNECTION_ERROR", responseCode.toString())
        }
        return ""
    }



}