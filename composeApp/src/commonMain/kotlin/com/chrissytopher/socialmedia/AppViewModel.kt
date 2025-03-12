package com.chrissytopher.socialmedia

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.asPainter
import coil3.request.ImageRequest
import com.liftric.kvault.KVault
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.PermissionsController
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

abstract class AppViewModel(val kvault: KVault) : ViewModel() {
    val authenticationManager = AuthenticationManager(this)
    abstract val cacheManager: CacheManager
    open val apiClient = ServerApi(authenticationManager = authenticationManager)
    abstract val permissionsController: PermissionsController
    abstract val locationTracker: LocationTracker
    private val _likeIcon = mutableStateOf(kvault.int(LIKE_ICON_KEY) ?: 0)
    private val _quag = mutableStateOf(kvault.bool(QUAG_KEY) ?: true)
    val likeIcon: State<Int> = _likeIcon
    val quag: State<Boolean> = _quag

    fun toggleQuag() {
        _quag.value = !_quag.value
        kvault.set(QUAG_KEY, !_quag.value)
    }

    fun setLikeIcon(new: Int) {
        _likeIcon.value = new
        kvault.set(LIKE_ICON_KEY, new)
    }
    abstract val platformContext: PlatformContext

    var currentPosts: Flow<List<PostRepresentation>>? = null

    fun getPostRecommendations() {
        currentPosts = flow {
            val location = getLocation(locationTracker)
            val postIds = location?.let { apiClient.getRecommendations(it).getOrNullAndThrow() } ?: return@flow
            var posts by atomic(postIds.map { PostRepresentation(it, null, null) })
            emit(posts)
            for (i in posts.indices) {
//                coroutineScope {
                    val contentId = posts[i].contentId
                    val info = apiClient.getPostInfo(contentId).getOrNullAndThrow()
                    println("info: $info")
                    posts = posts.toMutableList().apply { set(i, get(i).copy(info = info)) }
                    emit(posts)
                    var media: Any? = cacheManager.getCachedPostMedia(contentId)
                    if (media == null) {
                        val postMediaUrl = apiClient.getPostMediaUrl(contentId)
                            .getOrNullAndThrow() ?: continue
                        media = runCatching { apiClient.httpClient.get(postMediaUrl).bodyAsBytes() }.getOrNullAndThrow() ?: continue

                        cacheManager.coroutineScope.launch {
                            (media as? ByteArray?)?.let { cacheManager.cacheMedia(contentId, it) }
                        }

                        if (info?.get("mime")?.jsonPrimitive?.content?.startsWith("image/") == true) {
                            media = ImageLoader(platformContext).execute(ImageRequest.Builder(platformContext).data(media).build()).image?.asPainter(platformContext)
                        }
                    }
                    posts = posts.toMutableList().apply { set(i, get(i).copy(media = media)) }
                    emit(posts)
//                }
            }
        }
    }
}

data class PostRepresentation(
    val contentId: String,
    val info: JsonObject?,
    val media: Any?,
)