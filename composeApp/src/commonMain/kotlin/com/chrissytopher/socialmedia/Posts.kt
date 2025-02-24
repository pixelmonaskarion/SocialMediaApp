package com.chrissytopher.socialmedia

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

@Composable
fun Post(postInfo: JsonObject, postMedia: Any?, modifier: Modifier = Modifier) {
    Column(modifier) {
        val postMime = postInfo["mime"]?.jsonPrimitive?.contentOrNull ?: "text/plain"
        Text(Json.encodeToString(postInfo))
        if (postMedia != null) {
            if (postMime.startsWith("text/")) {
                (postMedia as? String)?.let { Text(it) }
            }
            if (postMime.startsWith("image/")) {
                val painter = key(postMedia) { rememberAsyncImagePainter(postMedia, onState = {
                    (it as? AsyncImagePainter.State.Error)?.result?.throwable?.printStackTrace()
                }) }
                val painterStatus by painter.state.collectAsState()
                if (painterStatus is AsyncImagePainter.State.Loading) {
                    CircularProgressIndicator()
                } else {
                    Image(painter, "post media")
                }
            }
        } else {
            CircularProgressIndicator()
        }
    }
}