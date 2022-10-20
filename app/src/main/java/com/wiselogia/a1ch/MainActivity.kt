package com.wiselogia.a1ch

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.wiselogia.a1ch.databinding.ActivityMainBinding
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.util.*


class MainActivity : AppCompatActivity(), OnDataChangeListener {
    private lateinit var binding: ActivityMainBinding

    private val provider by lazy {
        (application as App).messagesProvider
    }

    private val messagesAdapter by lazy {
        MessagesAdapter {
            startActivity(Intent(this, ImageActivity::class.java).apply {
                putExtra("path", it.image)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val messageServiceIntent = Intent(this, SendNRecieveService::class.java)
        startService(messageServiceIntent)
        provider.registerDataChangeListener(this)
        binding.messages.apply {
            adapter = messagesAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
        binding.sendView.setOnClickListener {
            Retrofit.networkExecutor.execute {
                Retrofit.sendRequest(
                    "/1ch",
                    Message(
                        data = Data(text = Text(binding.textView.text.toString())),
                        time = System.currentTimeMillis() / 1000,
                        from = "Wiselogia"
                    )
                )
                binding.textView.text.clear()
            }
        }
        binding.galleryView.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 3)
        }
    }

    override fun onResume() {
        super.onResume()
        (application as App).messagesProvider.getMessages()
    }

    override fun onDestroy() {
        super.onDestroy()
        provider.unregisterCallback(this)
    }

    override fun onChange(data: List<UsefulMessageModel>) {
        messagesAdapter.addItems(data)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 3 && data != null && data.data != null) {
            Retrofit.networkExecutor.execute {
                val img = getImageFromStorage(data.data!!)
                val code = Date().time.toString()
                val file = getTempFile(img!!, code)
                sendImageMessage(file, UUID.randomUUID().toString())
            }
        }
    }

    private fun getImageFromStorage(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT < 28) {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(
                    this.contentResolver,
                    uri
                )
            } else {
                val source = ImageDecoder.createSource(this.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getTempFile(image: Bitmap, code: String): File {
        val file = File(this.cacheDir, "${code}temp.png")
        file.createNewFile()
        val bos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 0, bos)
        val bitmapData = bos.toByteArray()
        val fos = FileOutputStream(file)
        fos.write(bitmapData)
        fos.flush()
        fos.close()
        return file
    }
}

fun sendImageMessage(file: File, code: String): Int {
    val url = URL("http://213.189.221.170:8008/1ch")
    val connection = url.openConnection() as HttpURLConnection
    connection.apply {
        requestMethod = "POST"
        doInput = true
        doOutput = true
        connectTimeout = 2000
    }

    val boundary = "------$code------"
    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")

    val crlf = "\r\n"
    val json = "{\"from\":\"Wiselogia\"}"
    val outputStream = connection.outputStream
    val outputStreamWriter = OutputStreamWriter(outputStream)
    outputStream.use {
        outputStreamWriter.use {
            with(it) {
                append("--").append(boundary).append(crlf)
                append("Content-Disposition: form-data; name=\"json\"").append(crlf)
                append("Content-Type: application/json; charset=utf-8").append(crlf)
                append(crlf)
                append(json).append(crlf)
                flush()
                appendFile(file, boundary, outputStream)
                append(crlf)
                append("--").append(boundary).append("--").append(crlf)
            }
        }
    }
    val responseCode = connection.responseCode
    connection.disconnect()
    return responseCode
}

private fun OutputStreamWriter.appendFile(
    file: File,
    boundary: String,
    outputStream: OutputStream,
    crlf: String = "\r\n"
) {
    val contentType = URLConnection.guessContentTypeFromName(file.name)
    val fis = FileInputStream(file)
    fis.use {
        append("--").append(boundary).append(crlf)
        append("Content-Disposition: form-data; name=\"file\"; filename=\"${file.name}\"")
        append(crlf)
        append("Content-Type: $contentType").append(crlf)
        append("Content-Length: ${file.length()}").append(crlf)
        append("Content-Transfer-Encoding: binary").append(crlf)
        append(crlf)
        flush()

        val buffer = ByteArray(4096)

        var n: Int
        while (fis.read(buffer).also { n = it } != -1) {
            outputStream.write(buffer, 0, n)
        }
        outputStream.flush()
        append(crlf)
        flush()
    }
}

