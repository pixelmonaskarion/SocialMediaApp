package com.chrissytopher.socialmedia

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.asPainter
import coil3.request.ImageRequest
import com.liftric.kvault.KVault
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.PermissionsController
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

abstract class AppViewModel : ViewModel() {
    val authenticationManager = AuthenticationManager(this)
    abstract val cacheManager: CacheManager
    open val apiClient = ServerApi(authenticationManager = authenticationManager)
    abstract val kvault: KVault
    abstract val permissionsController: PermissionsController
    abstract val locationTracker: LocationTracker
    private val _likeIcon = mutableStateOf(kvault.int(LIKE_ICON_KEY) ?: 0)
    val likeIcon: State<Int> = _likeIcon

    fun setLikeIcon(new: Int) {
        _likeIcon.value = new
        kvault.set(LIKE_ICON_KEY, new)
    }
    abstract val platformContext: PlatformContext

    fun getPostRecommendations(): Flow<List<PostRepresentation>> {
        return flow {
            val location = getLocation(locationTracker)
            val postIds = location?.let { apiClient.getRecommendations(it).getOrNullAndThrow() } ?: return@flow
            val posts = postIds.map { PostRepresentation(it, null, null) }.toMutableList()
            emit(posts)
            for (i in posts.indices) {
                viewModelScope.launch {
                    val contentId = posts[i].contentId
                    val info = apiClient.getPostInfo(contentId).getOrNull()
                    posts[i] = posts[i].copy(info = info)
                    emit(posts)
                    var media: Any? = cacheManager.getCachedPostMedia(contentId)
                    if (media == null) {
                        val postMediaUrl = apiClient.getPostMediaUrl(contentId)
                            .getOrNullAndThrow() ?: return@launch
                        media = runCatching { apiClient.httpClient.get(postMediaUrl).bodyAsBytes() }.getOrNullAndThrow() ?: return@launch

                        cacheManager.coroutineScope.launch {
                            (media as? ByteArray?)?.let { cacheManager.cacheMedia(contentId, it) }
                        }

                        if (info?.get("mime")?.jsonPrimitive?.content?.startsWith("image/") == true) {
                            media = ImageLoader(platformContext).execute(ImageRequest.Builder(platformContext).data(media).build()).image?.asPainter(platformContext)
                        }
                    }
                    posts[i] = posts[i].copy(media = media)
                    emit(posts)
                }
            }
        }
    }
}

data class PostRepresentation(
    val contentId: String,
    val info: JsonObject?,
    val media: Any?,
)