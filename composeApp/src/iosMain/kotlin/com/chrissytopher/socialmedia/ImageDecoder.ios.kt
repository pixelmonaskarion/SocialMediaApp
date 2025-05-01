package com.chrissytopher.socialmedia

import androidx.compose.ui.graphics.ImageBitmap
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap

@OptIn(ExperimentalResourceApi::class)
actual fun decodedImage(imageData: ByteArray): ImageBitmap {
    return imageData.decodeToImageBitmap()
}