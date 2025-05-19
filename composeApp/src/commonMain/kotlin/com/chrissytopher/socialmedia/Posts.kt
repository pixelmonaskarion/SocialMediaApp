package com.chrissytopher.socialmedia

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.float
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

@Composable
fun Post(
    postInfo: JsonObject,
    postMedia: Any?,
    likeIcon: Int,
    apiClient: ServerApi,
    myUsername: String,
    modifier: Modifier = Modifier,
    viewModel: AppViewModel
) {
    var coroutineScope = rememberCoroutineScope()
    val username = runCatching { postInfo["username"]?.jsonPrimitive?.contentOrNull }.getOrNull()
    Column(modifier.fillMaxWidth().padding(8.dp).clip(MaterialTheme.shapes.extraLarge).background(MaterialTheme.colorScheme.surfaceContainer)) {
        val postMime = postInfo["mime"]?.jsonPrimitive?.contentOrNull ?: "text/plain"
        if (postMedia != null) {
            if (postMime.startsWith("image/")) {
                val painter = key(postMedia) {
                    if (postMedia !is Painter) {
                        println("non painter $postMedia")
                        rememberAsyncImagePainter(postMedia, onState = {
                            (it as? AsyncImagePainter.State.Error)?.result?.throwable?.printStackTrace()
                        })
                    } else {
                        postMedia
                    }
                }
                val loading by ((painter as? AsyncImagePainter)?.state?.map { it is AsyncImagePainter.State.Loading } ?: MutableStateFlow(false)).collectAsState(true)
                val aspectRatio = runCatching { painter.intrinsicSize.width/painter.intrinsicSize.height }.getOrThrow()
                Column(Modifier.fillMaxWidth()) {
                    Box(Modifier.align(Alignment.CenterHorizontally), contentAlignment = Alignment.Center) {
                        if (loading) {
                            CircularProgressIndicator(Modifier.fillMaxWidth().aspectRatio(aspectRatio))
                        } else {
                            Image(
                                painter,
                                "post media",
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier.background(Color.Black).fillMaxWidth().aspectRatio(aspectRatio)
                            )
                        }
                    }
                    val caption = runCatching { postInfo["caption"]?.jsonPrimitive?.contentOrNull }.getOrNull()
                    var liked by remember { mutableStateOf(false) }
                    var likeCount by remember { mutableStateOf(-1) }
                    LaunchedEffect(liked) {
                        launch {
                            postInfo["content_id"]?.jsonPrimitive?.contentOrNull?.let { id -> apiClient.getLikes(id).getOrNullAndThrow()?.let { likes ->
                                println("likes: $likes")
                                liked = likes.find { it.user == myUsername } != null
                                likeCount = likes.size
                            } }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {


                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            var xPer by remember { mutableStateOf(0.5f) }
                            var yPer by remember { mutableStateOf(0.5f) }
                            var scale by remember { mutableStateOf(1f) }
                            var size by remember { mutableStateOf(400) }
                            var tempImage: Any? by remember { mutableStateOf(null) }
                            if (tempImage == null) {
                                coroutineScope.launch {
                                    tempImage = viewModel.getIconMedia(username!!)
                                    if (tempImage == null){
                                        println("default icon set")
                                        tempImage = viewModel.byteArrayFromUrl(
                                            "https://th.bing.com/th/id/R.6c6b24cc13711c81b8195195207f1143?rik=hjdqSBYcOWjO6A&riu=http%3a%2f%2fpulpbits.net%2fwp-content%2fuploads%2f2014%2f01%2fTabby-Cat-Images.jpg&ehk=%2fZ888cHTxBhWmeBDM7txDTST1TTI3Tf6lSCHOQO9tDg%3d&risl=&pid=ImgRaw&r=0")
                                    }
                                    val tempJson = viewModel.getIconInfoUser(username)
                                    println("json is "+tempJson.toString()+"username is :" + username)
                                    if (tempJson != null) {
                                        xPer = tempJson["x_percent"]?.jsonPrimitive?.float!!
                                        yPer = tempJson["y_percent"]?.jsonPrimitive?.float!!
                                        scale = tempJson["icon_scale"]?.jsonPrimitive?.float!!
                                        size = tempJson["icon_size"]?.jsonPrimitive?.int!!
                                    }
                                    viewModel.forceIconUserRefresh(username)
                                }
                            }
                            if (tempImage != null) {
                                val iconMedia = rememberAsyncImagePainter(tempImage, onState = {
                                    (it as? AsyncImagePainter.State.Error)?.result?.throwable?.printStackTrace()
                                })
                                val iconLoading by ((painter as? AsyncImagePainter)?.state?.map { it is AsyncImagePainter.State.Loading }
                                    ?: MutableStateFlow(false)).collectAsState(true)
                                val aspectRatio = 1f
                                val actualSize = 150
                                val actualScale = (scale * actualSize.toFloat())/ (size.toFloat())
                                if (iconLoading) {
                                    CircularProgressIndicator(
                                        Modifier.size(actualSize.dp).aspectRatio(aspectRatio)
                                    )
                                } else {
                                    Image(
                                        iconMedia, contentDescription = "icon",
                                        modifier = croppingScream(
                                            x = xPer,
                                            y = yPer,
                                            actualScale,
                                            actualSize
                                        )
                                    )
                                }
                            }
                            Column(Modifier.padding(10.dp, 0.dp)) {
                                Text(username ?: "", style = MaterialTheme.typography.labelLarge)
                                Text(caption ?: "", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    postInfo["content_id"]?.jsonPrimitive?.contentOrNull?.let { id ->
                                        apiClient.setAttribute(
                                            id,
                                            "POST_LIKE",
                                            Json.encodeToString(!liked)
                                        ).getOrNullAndThrow()
                                    }
                                    liked = !liked
                                }
                            }) {
                                Icon(
                                    if (!liked) {
                                        likeIcons[likeIcon].first
                                    } else {
                                        likeIcons[likeIcon].second
                                    }, "Like Button"
                                )
                            }
                            if (likeCount != -1) {
                                Text("$likeCount", style = MaterialTheme.typography.bodyLarge)
                            } else {
                                CircularProgressIndicator(Modifier.size(with(LocalDensity.current) { MaterialTheme.typography.bodyLarge.fontSize.toDp() }))
                            }
                            Spacer(Modifier.width(8.dp))
                        }
                    }
                    }
                }

        } else {
            CircularProgressIndicator()
        }
    }
}