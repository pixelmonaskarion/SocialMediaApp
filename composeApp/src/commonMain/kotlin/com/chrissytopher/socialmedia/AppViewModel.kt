package com.chrissytopher.socialmedia

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.float
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

abstract class AppViewModel(val kvault: KVault) : ViewModel() {
    val authenticationManager = AuthenticationManager(this)
    abstract val cacheManager: CacheManager
    open val apiClient = ServerApi(authenticationManager = authenticationManager)
    abstract val permissionsController: PermissionsController
    abstract val locationTracker: LocationTracker
    private val _settingFormat = mutableStateOf(kvault.int(SETTING_FORMAT_KEY) ?: 0)
    private val _likeIcon = mutableStateOf(kvault.int(LIKE_ICON_KEY) ?: 0)
    private val _quag = mutableStateOf(kvault.bool(QUAG_KEY) ?: true)
    private val _romantical = mutableStateOf(kvault.bool(ROMANTICAL_KEY) ?: false)
    private val _darkMode = mutableStateOf(kvault.bool(DARK_MODE_KEY))
    val settingFormat: State<Int> = _settingFormat

    private val _iconImageLink = mutableStateOf(kvault.string(ICON_PIC_STRING)?:"")
    private val _iconXPer = mutableStateOf(kvault.float(ICON_X_PER)?: 0.5f)
    private val _iconYPer = mutableStateOf(kvault.float(ICON_Y_PER)?: 0.5f)
    private val _iconScale = mutableStateOf(kvault.float(ICON_SCALE)?: 1f)
    private val _iconOutSize = mutableStateOf(kvault.int(ICON_OUTPUT)?: 200)


    private val _cropOffsetX = mutableStateOf(kvault.float(CROP_OFFSETX)?:0f)
    private val _cropOffsetY = mutableStateOf(kvault.float(CROP_OFFSETY)?:0f)
    private val _cropSize = mutableStateOf(kvault.float(CROP_SIZE)?:10f)

    val likeIcon: State<Int> = _likeIcon
    val quag: State<Boolean> = _quag
    val romantical: State<Boolean> = _romantical
    val darkMode: State<Boolean?> = _darkMode

    fun setSettingFormat(new: Int) {
        _settingFormat.value = new
        viewModelScope.launch {
            kvault.set(SETTING_FORMAT_KEY, new)
        }
    }


    fun setLikeIcon(new: Int) {
        _likeIcon.value = new
        viewModelScope.launch {
            kvault.set(LIKE_ICON_KEY, new)
        }
    }
    fun toggleQuag() {
        _quag.value = !_quag.value
        viewModelScope.launch {
            kvault.set(QUAG_KEY, !_quag.value)
        }
    }
    fun toggleRomantical() {
        _romantical.value = !_romantical.value
        viewModelScope.launch {
            kvault.set(ROMANTICAL_KEY, !_romantical.value)
        }
    }

    fun setDarkMode(new: Boolean) {
        _darkMode.value = new
        viewModelScope.launch {
            kvault.set(DARK_MODE_KEY, new)
        }
    }
    val iconXPer:State<Float> = _iconXPer
    val iconImageLink:State<String?> = _iconImageLink
    val iconYPer:State<Float> = _iconYPer
    val iconScale:State<Float> = _iconScale
    val iconOutputSize:State<Int> = _iconOutSize

    val cropOffsetX:State<Float> = _cropOffsetX
    val cropOffsetY:State<Float> = _cropOffsetY
    val cropSize:State<Float> = _cropSize
    fun changeIconImage(xPer:Float, yPer:Float,imageScale:Float,imageSize:Int,imageLink:String?){
        _iconXPer.value = xPer
        kvault.set(ICON_X_PER,xPer)
        _iconYPer.value = yPer
        kvault.set(ICON_Y_PER,yPer)
        _iconScale.value = imageScale
        kvault.set(ICON_SCALE,imageScale)
        _iconOutSize.value = imageSize
        kvault.set(ICON_OUTPUT,imageSize)
        if (imageLink != null){
            _iconImageLink.value = imageLink
            kvault.set(ICON_PIC_STRING,imageLink)
        }

    }
//    fun fetchIconSettings(username:String){
//        viewModelScope.launch {
//            val json = apiClient.getIconInfo(username).getOrNull()
//            if (json != null) {
//                val xPer = json["x_percent"]?.jsonPrimitive?.float
//                if (xPer != null) {
//                    _iconXPer.value = xPer
//                    kvault.set(ICON_X_PER,xPer)
//                }
//                val yPer = json["y_percent"]?.jsonPrimitive?.float
//                if (yPer != null) {
//                    _iconYPer.value = yPer
//                    kvault.set(ICON_Y_PER,yPer)
//                }
//                val imageScale = json["icon_scale"]?.jsonPrimitive?.float
//                if (imageScale != null) {
//                    _iconScale.value = imageScale
//                    kvault.set(ICON_SCALE,imageScale)
//                }
//                val imageSize = json["icon_size"]?.jsonPrimitive?.int
//                if (imageSize != null) {
//                    _iconOutSize.value = imageSize
//                    kvault.set(ICON_OUTPUT,imageSize)
//                }
//
//            }
//        }
//
//    }
    fun updateIconImage(imageLink:String?){
        if (imageLink != null){
            _iconImageLink.value = imageLink
            kvault.set(ICON_PIC_STRING,imageLink)
        }
    }
    fun changeCropSetting(offsetX:Float,offsetY:Float,cropSize:Float){
        _cropOffsetX.value = offsetX
        kvault.set(CROP_OFFSETX,offsetX)
        _cropOffsetY.value=offsetY
        kvault.set(CROP_OFFSETY,offsetY)
        _cropSize.value = cropSize
        kvault.set(CROP_SIZE,cropSize)
    }

    abstract val platformContext: PlatformContext

    val currentPosts: MutableStateFlow<List<PostRepresentation>> = MutableStateFlow(emptyList())
    private val _isLoadingPosts: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoadingPosts = _isLoadingPosts.asStateFlow()
//    init {
//        viewModelScope.launch {
//            getPostRecommendations()
//        }
//    }

    suspend fun getIconInfoUser(username: String):JsonObject?{
        val info = cacheManager.getCachedIconInfo(username) ?: apiClient.getIconInfo(username).getOrNull()
        if (info != null){
            viewModelScope.launch {
                cacheManager.cacheInfo(username, info)
            }
        }
        return info
    }
    suspend fun getIconMedia(username: String):ByteArray?{
        var media: Any? = cacheManager.getCachedIconMedia(username)
        println("cached media is"+ media.toString())
        if (media == null){
            val iconMediaUrl = apiClient.getIconUrl(username).getOrNullAndThrow()
            println("media url is: $iconMediaUrl")
            if (iconMediaUrl != null) {
                media = runCatching { apiClient.httpClient.get(iconMediaUrl).bodyAsBytes() }.getOrNullAndThrow()
                cacheManager.coroutineScope.launch {
                    (media as? ByteArray?)?.let { cacheManager.cacheIconMedia(username, it) }
                }
            }
        }
        return media as? ByteArray?
        }
    suspend fun byteArrayFromUrl(url: String): ByteArray?{
        return runCatching { apiClient.httpClient.get(url).bodyAsBytes() }.getOrNullAndThrow()
    }
    suspend fun forceIconUserRefresh(username:String)
    {
        println("forcing refresh for $username")
        val newInfo = apiClient.getIconInfo(username).getOrNull()
        if (newInfo != null) {
            viewModelScope.launch {
                cacheManager.cacheIconInfo(username,newInfo)
            }
        }
        val iconMediaUrl = apiClient.getIconUrl(username).getOrNull()
        if (iconMediaUrl != null) {
            val media = runCatching { apiClient.httpClient.get(iconMediaUrl).bodyAsBytes() }.getOrNullAndThrow()
            cacheManager.coroutineScope.launch {
                (media)?.let { cacheManager.cacheIconMedia(username, it) }
            }
        }


    }

    suspend fun offlineIconUpload(icon: ByteArray,username: String)
    {
        viewModelScope.launch {
            cacheManager.cacheIconMedia(username,icon)
        }

    }

    suspend fun getPostRecommendations() {
        _isLoadingPosts.value = true
        val location = getLocation(locationTracker)
        val postIds = location?.let { apiClient.getRecommendations(it).getOrNullAndThrow() } ?: return
        var posts by atomic(postIds.map { PostRepresentation(it, null, null) })
//            emit(posts)
        for (i in posts.indices) {
//                coroutineScope {
                val contentId = posts[i].contentId
                val info = cacheManager.getCachedPostInfo(contentId) ?: apiClient.getPostInfo(contentId).getOrNull()
                if (info != null) {
                    viewModelScope.launch {
                        cacheManager.cacheInfo(contentId, info)
                    }
                }
                posts = posts.toMutableList().apply { set(i, get(i).copy(info = info)) }
//                    emit(posts)
                var media: Any? = cacheManager.getCachedPostMedia(contentId)
                if (media == null) {
                    val postMediaUrl = apiClient.getPostMediaUrl(contentId)
                        .getOrNullAndThrow() ?: continue
                    media = runCatching { apiClient.httpClient.get(postMediaUrl).bodyAsBytes() }.getOrNullAndThrow() ?: continue

                    cacheManager.coroutineScope.launch {
                        (media as? ByteArray?)?.let { cacheManager.cacheMedia(contentId, it) }
                    }


                }
                if (info?.get("mime")?.jsonPrimitive?.content?.startsWith("image/") == true) {
                    media = ImageLoader(platformContext).execute(ImageRequest.Builder(platformContext).data(media).build()).image?.asPainter(platformContext)
                }
                posts = posts.toMutableList().apply { set(i, get(i).copy(media = media)) }
                currentPosts.value = posts
//                    emit(posts)
//                }
        }
        currentPosts.value = posts
        _isLoadingPosts.value = false
    }

    suspend fun loadImage(model: Any?): Painter? = ImageLoader(platformContext).execute(ImageRequest.Builder(platformContext).data(model).build()).image?.asPainter(platformContext)
}

data class PostRepresentation(
    val contentId: String,
    val info: JsonObject?,
    val media: Any?,
)

data class IconRepresenation(
    val username: String,
    val info: JsonObject?,
    val media: Any?
)