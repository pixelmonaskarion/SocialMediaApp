package com.chrissytopher.socialmedia

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun decodedImage(imageData: ByteArray): ImageBitmap {
    val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
    return bitmap.asImageBitmap()
}