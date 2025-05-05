package com.chrissytopher.socialmedia

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.chrissytopher.socialmedia.navigation.NavigationStack
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import kotlinx.coroutines.launch
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@Composable
fun pickIconScreen(viewModel: AppViewModel,navHost: NavigationStack<NavScreen>)
{
    Column(Modifier.padding(10.dp).fillMaxSize()) {
        val platform = LocalPlatform.current
        val coroutineScope = rememberCoroutineScope()
        val username = viewModel.authenticationManager.username
        val mime = remember { mutableStateOf("text/plain") }
        val image: MutableState<ByteArray?> = remember { mutableStateOf(null) }
        LaunchedEffect(image.value)
            {if (navHost.routeState.value == NavScreen.IconSelect && image.value == null) {
                coroutineScope.launch {
                    val pickedImageOrNah = platform.pickImages().firstOrNull()
                    if (pickedImageOrNah == null) {
                        navHost.popStack()
                    } else {
                        pickedImageOrNah.let { pickedImage ->
                            mime.value = "image/?"
                            image.value = pickedImage.readByteArray()
                            viewModel.apiClient.uploadIconMedia(image.value!!)
                            pickedImage.close()
                        }
                    }
                }
            }
        }
        LaunchedEffect(navHost.routeState.value) {
            if (navHost.routeState.value != NavScreen.IconSelect) {
                image.value = null
            }
        }
        image.value?.let { imageData ->
            val decodedImage = decodedImage(imageData)
            Image(
                bitmap = decodedImage,
                contentDescription = "selected image",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp)
                    .size(500.dp),
                contentScale = ContentScale.Crop
            )
        }
        val localSnackbar = LocalSnackbarState.current
        Button(onClick = {
            coroutineScope.launch {
                val iconInfo = JsonObject(hashMapOf(
                    "username" to JsonPrimitive(username),
                    "x_percent" to JsonPrimitive(0.5f),
                    "y_percent" to JsonPrimitive(0.5f),
                    "icon_scale" to JsonPrimitive(1f),
                    "icon_size" to JsonPrimitive(400),
                    "mime" to JsonPrimitive(mime.value)
                ))
                val res = viewModel.apiClient.uploadIconInfo(iconInfo)
                if (res.isSuccess) {
                    println("posting $username")
                    localSnackbar.showSnackbar("Locked in \uD83D\uDD25\uD83D\uDD25\uD83D\uDD1D\uD83D\uDD1F")
                    if (username != null) {
                        println("getting icon url for $username")
                        val result = viewModel.apiClient.getIconUrl(username)
                        println(result)
                        viewModel.updateIconImage(result.getOrDefault(""))
                    }
                    navHost.popStack()
                } else {
                    launch {
                        res.exceptionOrNull()?.printStackTrace()
                        localSnackbar.showSnackbar("Tweaked \uD83D\uDE14, $res")
                    }
                }
            }
        }
        ) { Text("Post!") }
    }
}
