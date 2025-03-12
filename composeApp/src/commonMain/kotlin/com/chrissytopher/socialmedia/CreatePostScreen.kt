package com.chrissytopher.socialmedia

import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.chrissytopher.socialmedia.navigation.NavigationController
import com.chrissytopher.socialmedia.navigation.NavigationStack
import dev.icerock.moko.geo.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.jetbrains.compose.resources.painterResource
import coil3.compose.AsyncImage
import socialmediaapp.composeapp.generated.resources.Res
import socialmediaapp.composeapp.generated.resources.dancing_quag
import androidx.compose.ui.graphics.Color


@Composable
fun CreatePostScreen(viewModel: AppViewModel, navHost: NavigationStack<NavScreen>) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.dancing_quag),
            contentDescription = "BG Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
    Column(Modifier.padding(10.dp)) {
        val platform = LocalPlatform.current
        val coroutineScope = rememberCoroutineScope()
        val contentIdState: MutableStateFlow<String?> = remember { MutableStateFlow(null) }
        val contentId by contentIdState.collectAsState(null)
        println("content-id: |${contentId}|")
        val mime = remember { mutableStateOf("text/plain") }
        val image: MutableState<ByteArray?> = remember { mutableStateOf(null) }
        LaunchedEffect(contentId) {
            if (contentId == null && navHost.routeState.value == NavScreen.CreatePost) {
                coroutineScope.launch {
                    val pickedImageOrNah = platform.pickImages().firstOrNull()
                    if (pickedImageOrNah == null) {
                        navHost.popStack()
                    } else {
                        pickedImageOrNah.let { pickedImage ->
                            contentIdState.value = ""
                            mime.value = "image/?"
                            image.value = pickedImage.readByteArray()
                            contentIdState.value = viewModel.apiClient.uploadPostMedia(image.value!!).getOrNullAndThrow()
                            pickedImage.close()
                        }
                    }
                }
            }
        }
        LaunchedEffect(navHost.routeState.value) {
            if (navHost.routeState.value != NavScreen.CreatePost) {
                contentIdState.value = null
            }
        }
        if (contentId != null) {
            var location: LatLng? by key(contentId) { remember { mutableStateOf(null) } }
            LaunchedEffect(contentId) {
                if (location == null) {
                    launch { location = getLocation(viewModel.locationTracker) }
                }
            }
            var caption by remember { mutableStateOf("") }
            val keyboardController = LocalSoftwareKeyboardController.current
            OutlinedTextField(caption, onValueChange = {
                caption = it },
                label = { Text("Caption") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
//                alignment = Alignment.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
            val localSnackbar = LocalSnackbarState.current
            Button(onClick = {
                keyboardController?.hide()
                coroutineScope.launch {
                    if (location == null) {
                        location = getLocation(viewModel.locationTracker)
                    }
                    val postInfo = Json.encodeToString(JsonObject(hashMapOf(
                        "content_id" to JsonPrimitive(contentId),
                        "caption" to JsonPrimitive(caption),
                        "location" to JsonPrimitive(location?.let { locationFormatted(it) }),
                        "username" to JsonPrimitive(viewModel.authenticationManager.username),
                        "mime" to JsonPrimitive(mime.value)
                    )))
                    val res = viewModel.apiClient.uploadPostInfo(postInfo)
                    if (res.isSuccess) {
                        navHost.popStack()
                        localSnackbar.showSnackbar("Locked in \uD83D\uDD25\uD83D\uDD25\uD83D\uDD1D\uD83D\uDD1F")
                    } else {
                        localSnackbar.showSnackbar("Tweaked \uD83D\uDE14, $res")
                    }
                }
            }) {
                Text("Post!")
            }
        }
    }
}