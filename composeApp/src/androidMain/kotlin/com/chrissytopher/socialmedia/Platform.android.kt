package com.chrissytopher.socialmedia

import android.content.Context
import com.liftric.kvault.KVault
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

class AndroidPlatform(private val context: Context): Platform() {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
    override val apiClient = ServerApi(
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json()
            }
        }
    )
    override val kvault = KVault(context)
}

