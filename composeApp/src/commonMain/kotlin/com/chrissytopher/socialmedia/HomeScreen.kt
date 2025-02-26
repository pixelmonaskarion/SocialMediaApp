package com.chrissytopher.socialmedia

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

@Composable
fun HomeScreen() {
    Box(Modifier.fillMaxSize().padding(10.dp)) {
        Column {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Social Media App", style = MaterialTheme.typography.titleLarge)
                Row(
                    Modifier.width(130.dp).height(50.dp).padding(20.dp, 0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(Modifier.size(45.dp).clip(CircleShape).selectable(false) {

                    }) {
                        Icon(Icons.Outlined.FavoriteBorder, null, Modifier.align(Alignment.Center))
                    }
                    Box(Modifier.size(45.dp).clip(CircleShape).selectable(false) {

                    }) {
                        Icon(
                            Icons.Outlined.ChatBubbleOutline,
                            null,
                            Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
            Row(Modifier.height(100.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.SpaceBetween) {
                for(i in 1..5) {
                    Icon(Icons.Rounded.AccountCircle, null, Modifier.size(100.dp))
                }
            }
            val platform = LocalPlatform.current
            val cacheManager = LocalCacheManager.current
            var postIds: List<String>? by remember { mutableStateOf(null) }
            val posts: SnapshotStateList<Pair<JsonObject?, ByteArray?>> = key(postIds) { remember { mutableStateListOf(*(postIds?.map { Pair(null, null) }?.toTypedArray() ?: emptyArray())) } }
            val coroutineScope = rememberCoroutineScope()
            val locationTracker = platform.getLocationTracker(LocalPermissionsController.current)
            LaunchedEffect(true) {
                coroutineScope.launch {
                    val location = getLocation(locationTracker)
                    postIds = location?.let { platform.apiClient.getRecommendations(it).getOrNullAndThrow() }
                }
            }
            postIds?.let {
                LazyColumn {
                    itemsIndexed(postIds ?: emptyList()) { i, contentId ->
                        val itemCoroutineScope = rememberCoroutineScope()
                        val (postInfo, postMedia) = posts[i]
                        LaunchedEffect(contentId) {
                            itemCoroutineScope.launch {
                                var newPostInfo = cacheManager.getCachedPostInfo(contentId)
                                if (newPostInfo == null) {
                                    newPostInfo = platform.apiClient.getPostInfo(contentId).getOrNullAndThrow() ?: return@launch
                                    cacheManager.coroutineScope.launch {
                                        cacheManager.cacheInfo(contentId, newPostInfo)
                                    }
                                }
                                posts[i] = Pair(newPostInfo, postMedia)
                                var media: ByteArray? = cacheManager.getCachedPostMedia(contentId)
                                if (media == null) {
                                    val postMediaUrl = platform.apiClient.getPostMediaUrl(contentId)
                                        .getOrNullAndThrow() ?: return@launch
                                    media = runCatching { platform.apiClient.httpClient.get(postMediaUrl).bodyAsBytes() }.getOrNullAndThrow("media download failed") ?: return@launch
                                    cacheManager.coroutineScope.launch {
                                        cacheManager.cacheMedia(contentId, media)
                                    }
                                }
                                posts[i] = Pair(newPostInfo, media)
                            }
                        }
                        key(contentId) {
                            postInfo?.let {
                                Post(it, postMedia)
                            }
                        }
                    }
                }
            }
        }
    }
}