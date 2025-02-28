package com.chrissytopher.socialmedia

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil3.PlatformContext
import com.liftric.kvault.KVault
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.PermissionsController
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.io.files.Path

class AndroidAppViewModel(private val applicationContext: Context, override val permissionsController: PermissionsController, override val locationTracker: LocationTracker) : AppViewModel() {
    override val kvault: KVault = KVault(applicationContext)
    override val apiClient: ServerApi = ServerApi(
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json()
            }
        }, authenticationManager
    )
    override val platformContext: PlatformContext = applicationContext
    override val cacheManager: CacheManager = CacheManager(Path(applicationContext.cacheDir.absolutePath), viewModelScope)

    companion object {
        fun factory(applicationContext: Context, permissionsController: PermissionsController, locationTracker: LocationTracker) : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AndroidAppViewModel(
                    applicationContext,
                    permissionsController,
                    locationTracker
                )
            }
        }
    }
}