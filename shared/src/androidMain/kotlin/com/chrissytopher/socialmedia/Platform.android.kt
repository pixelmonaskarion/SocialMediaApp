package com.chrissytopher.socialmedia

import android.os.Build

class AndroidPlatform : Platform() {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}