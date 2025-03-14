package com.chrissytopher.socialmedia

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

@Composable
fun Post(postInfo: JsonObject, postMedia: Any?, likeIcon: Int, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        val postMime = postInfo["mime"]?.jsonPrimitive?.contentOrNull ?: "text/plain"
        //Text(Json.encodeToString(postInfo))
        if (postMedia != null) {
            if (postMime.startsWith("image/")) {
                val painter = key(postMedia) {
                    if (postMedia !is Painter) {
                        rememberAsyncImagePainter(postMedia, onState = {
                            (it as? AsyncImagePainter.State.Error)?.result?.throwable?.printStackTrace()
                        })
                    } else {
                        postMedia
                    }
                }
                val loading by ((painter as? AsyncImagePainter)?.state?.map { it is AsyncImagePainter.State.Loading } ?: MutableStateFlow(false)).collectAsState(true)
                Column(Modifier.fillMaxWidth()) {
                    if (loading) {
                        CircularProgressIndicator()
                    } else {
                        Image(
                            painter,
                            "post media",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.clip(shape = MaterialTheme.shapes.extraLarge).align(Alignment.CenterHorizontally).fillMaxWidth()
                        )
                    }
                    var caption = runCatching { postInfo["caption"]?.jsonPrimitive?.contentOrNull }.getOrNull()
                    Text(caption ?: ""  , Modifier.padding(10.dp, 0.dp), style = MaterialTheme.typography.bodyMedium)
                    var liked by remember { mutableStateOf(false) }
                    Row {
                        IconButton(onClick = {
                            liked = !liked
                        }) {
                            Icon(
                                if (!liked) {
                                    likeIcons[likeIcon].first
                                } else {
                                    likeIcons[likeIcon].second
                                }, "Like Button")
                        }
                    }
                }
            }
        } else {
            CircularProgressIndicator()
        }
    }
}