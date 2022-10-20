package com.wiselogia.a1ch


import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

fun ShapeableImageView.glide(path: String) {
    Glide.with(this)
        .load("http://213.189.221.170:8008$path")
        .error(R.drawable.loutre2)
        .centerCrop()
        .into(this)

}

fun ImageView.glide(path: String) {
    Glide.with(this)
        .load("http://213.189.221.170:8008$path")
        .error(R.drawable.loutre2)
        .centerCrop()
        .into(this)

}