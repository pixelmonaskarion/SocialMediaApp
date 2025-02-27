package com.chrissytopher.socialmedia

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.liftric.kvault.KVault
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.io.Source

abstract class AppViewModel : ViewModel() {
    val authenticationManager = AuthenticationManager(this)
    abstract val cacheManager: CacheManager
    open val apiClient = ServerApi(authenticationManager = authenticationManager)
    abstract val kvault: KVault
    abstract val permissionsController: PermissionsController
    abstract val locationTracker: LocationTracker
}