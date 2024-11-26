package com.chrissytopher.socialmedia

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

class AndroidPlatform: Platform() {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
    override val apiClient = ServerApi(HttpClient(OkHttp))
}

