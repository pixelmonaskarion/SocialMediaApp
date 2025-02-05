package com.chrissytopher.socialmedia

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.geo.LatLng
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@Composable
fun CreatePostScreen() {
    Column(Modifier.padding(10.dp)) {
        val platform = LocalPlatform.current
        val coroutineScope = rememberCoroutineScope()
        var contentId: String? by remember { mutableStateOf(null) }
        if (contentId == null) {
            Button(onClick = {
                coroutineScope.launch {
                    contentId = platform.apiClient.uploadPostMedia("Hello World!".encodeToByteArray())
                }
            }) {
                Text("Create Test Post")
            }
        } else {
            var location: LatLng? by key(contentId) { remember { mutableStateOf(null) } }
            if (location == null) {
                val locationTracker = platform.getLocationTracker(LocalPermissionsController.current)
                Button(onClick = {
                    coroutineScope.launch { location = getLocation(locationTracker) }
                }) {
                    Text("Get Location")
                }
            } else {
                var postInfo by key(location) { remember { mutableStateOf(Json.encodeToString(JsonObject(hashMapOf(
                    "content_id" to JsonPrimitive(contentId),
                    "location" to JsonPrimitive(location?.let { locationFormatted(it) }),
                    "username" to JsonPrimitive(platform.authenticationManager.username),
                )))) } }
                OutlinedTextField(postInfo, onValueChange = {
                    postInfo = it
                })
                val localSnackbar = LocalSnackbarState.current
                Button(onClick = {
                    coroutineScope.launch {
                        val res = platform.apiClient.uploadPostInfo(postInfo)
                        if (res.isSuccess) {
                            location = null
                            contentId = null
                            localSnackbar.showSnackbar("Locked in \uD83D\uDD25\uD83D\uDD25\uD83D\uDD1D\uD83D\uDD1F")
                        } else {
                            localSnackbar.showSnackbar("Tweaked \uD83D\uDE14, $res")
                        }
                    }
                }, enabled = runCatching { Json.decodeFromString<JsonObject>(postInfo) }.isSuccess) {
                    Text("Post!")
                }
            }
        }
    }
}