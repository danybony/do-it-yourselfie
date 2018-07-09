package net.bonysoft.doityourselfie

import android.support.annotation.IdRes
import android.view.View
import android.widget.ImageView
import android.widget.TextView

fun View.textView(@IdRes id: Int): TextView = findViewById(id)

fun View.imageView(@IdRes id: Int): ImageView = findViewById(id)