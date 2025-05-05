package com.chrissytopher.socialmedia

import androidx.lifecycle.viewModelScope
import coil3.PlatformContext
import com.liftric.kvault.KVault
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.PermissionsController
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.files.Path
import platform.Foundation.*

class IosAppViewModel() : AppViewModel(KVault()) {
    override val apiClient: ServerApi = ServerApi(
        HttpClient {
            install(ContentNegotiation) {
                json()
            }
        }, authenticationManager
    )
    override val cacheManager: CacheManager = CacheManager(Path(documentDirectory(), "cache"), viewModelScope)
    override val platformContext: PlatformContext = PlatformContext.INSTANCE
    override val permissionsController = dev.icerock.moko.permissions.ios.PermissionsController()
    override val locationTracker: LocationTracker = LocationTracker(permissionsController)
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}