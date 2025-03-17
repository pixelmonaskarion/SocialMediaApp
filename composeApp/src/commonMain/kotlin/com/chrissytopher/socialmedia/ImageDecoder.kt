package com.chrissytopher.socialmedia

import androidx.compose.ui.graphics.ImageBitmap

expect fun decodeByteArrayToImageBitmap(byteArray: ByteArray): ImageBitmap
expect fun decodedImage(imageData: ByteArray): ImageBitmap