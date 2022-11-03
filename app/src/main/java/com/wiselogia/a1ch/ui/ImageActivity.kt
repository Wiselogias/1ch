package com.wiselogia.a1ch.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wiselogia.a1ch.databinding.ImageActivityBinding
import com.wiselogia.a1ch.glide

class ImageActivity : AppCompatActivity() {
    private lateinit var binding: ImageActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ImageActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView.glide("/img/${intent.getStringExtra("path")}")
    }
}