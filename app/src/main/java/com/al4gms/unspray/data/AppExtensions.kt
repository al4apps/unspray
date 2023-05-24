package com.al4gms.unspray.data

import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.al4gms.unspray.R
import com.bumptech.glide.Glide

fun ImageView.setImageWithDefaultPlaceholderGlide(view: View, url: String) {
    Glide.with(view)
        .load(url)
        .placeholder(R.drawable.placeholder2)
        .into(this)
}
