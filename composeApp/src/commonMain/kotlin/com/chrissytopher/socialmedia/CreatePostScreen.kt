package com.chrissytopher.socialmedia

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CreatePostScreen() {
    val platform = LocalPlatform.current
    val coroutineScope = rememberCoroutineScope()
    Button(onClick = {
        coroutineScope.launch {
            val contentId = platform.apiClient.uploadPostMedia("Hello World!".encodeToByteArray())
            println("contentId: $contentId")
        }
    }) {
        Text("Create Test Post")
    }
}