package com.chrissytopher.socialmedia

import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.geo.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive


@Composable
fun CreatePostScreen() {
    Column(Modifier.padding(10.dp)) {
        val platform = LocalPlatform.current
        val coroutineScope = rememberCoroutineScope()
        val contentIdState: MutableStateFlow<String?> = remember { MutableStateFlow(null) }
        val contentId by contentIdState.collectAsState(null)
        println("content-id: |${contentId}|")
        val mime = remember { mutableStateOf("text/plain") }
        val image: MutableState<ByteArray?> = remember { mutableStateOf(null) }
        LaunchedEffect(contentId) {
            if (contentId == null) {
                coroutineScope.launch {
                    platform.pickImages().firstOrNull()?.let { pickedImage ->
                        contentIdState.value = ""
                        mime.value = "image/?"
                        image.value = pickedImage.readByteArray()
                        contentIdState.value = platform.apiClient.uploadPostMedia(image.value!!).getOrNullAndThrow()
                        pickedImage.close()
                    }
                }
            }
        }
        if (contentId != null) {
            var location: LatLng? by key(contentId) { remember { mutableStateOf(null) } }
            val locationTracker = platform.getLocationTracker(LocalPermissionsController.current)
            LaunchedEffect(location) {
                if (location == null) {
                    launch { location = getLocation(locationTracker) }
                }
            }
            var caption by remember { mutableStateOf("") }
            OutlinedTextField(caption, onValueChange = {
                caption = it
            }, label = { Text("Caption") })
            var postInfo by key(location) { remember { mutableStateOf(Json.encodeToString(JsonObject(hashMapOf(
                "content_id" to JsonPrimitive(contentId),
                "caption" to JsonPrimitive(caption),
                "location" to JsonPrimitive(location?.let { locationFormatted(it) }),
                "username" to JsonPrimitive(platform.authenticationManager.username),
                "mime" to JsonPrimitive(mime.value)
            )))) } }
            println("postInfo: $postInfo")
            val localSnackbar = LocalSnackbarState.current
            Button(onClick = {
                coroutineScope.launch {
                    val res = platform.apiClient.uploadPostInfo(postInfo)
                    if (res.isSuccess) {
                        location = null
                        contentIdState.value = null
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