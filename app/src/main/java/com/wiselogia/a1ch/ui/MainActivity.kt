package com.wiselogia.a1ch.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import com.wiselogia.a1ch.*
import com.wiselogia.a1ch.databinding.ActivityMainBinding
import com.wiselogia.a1ch.models.SendModel
import com.wiselogia.a1ch.services.SendNRecieveService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, t ->
        t.printStackTrace()
    }
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
        binding.messages.apply {
            adapter = messagesAdapter
        }
        lifecycle.coroutineScope.launchWhenStarted {
            provider.getMessages().collect {
                messagesAdapter.showables = it
            }
        }
        binding.sendView.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    (application as App).api.postTextMessage(
                        SendModel(
                            from = "Wiselogia",
                            data = Data(Text = Text(binding.textView.text.toString()))
                        )
                    )
                }
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 3 && data != null && data.data != null) {
            lifecycleScope.launch {
                withContext(Dispatchers.IO + coroutineExceptionHandler) {
                    val img = if (Build.VERSION.SDK_INT < 28) {
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(
                            this@MainActivity.contentResolver,
                            data.data
                        )
                    } else {
                        val source =
                            ImageDecoder.createSource(this@MainActivity.contentResolver, data.data!!)
                        ImageDecoder.decodeBitmap(source)
                    }

                    sendImage(img)
                }
            }
        }
    }

    suspend fun sendImage(img: Bitmap?) {
        val code = Date().time.toString()
        val file = fileCreate(img!!, code)
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        val body = MultipartBody.Part.createFormData("picture", file.name ,requestFile);
        val requestJson =  RequestBody.create(MediaType.parse("application/json"), "{\"from\":\"Wiselogia\"}");
        val json = MultipartBody.Part.createFormData("json", "", requestJson)
        (application as App).api.postImageMessage(json, body)
    }

    private fun fileCreate(image: Bitmap, code: String): File {
        val file = File(this.cacheDir, "${code}temp.png")
        file.createNewFile()
        val outputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
        val bitmapData = outputStream.toByteArray()
        val fileOutputStream = FileOutputStream(file)
        fileOutputStream.write(bitmapData)
        fileOutputStream.flush()
        fileOutputStream.close()
        return file
    }
}