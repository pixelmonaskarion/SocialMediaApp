package com.chrissytopher.socialmedia

import coil3.PlatformContext
import com.liftric.kvault.KVault
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.PermissionsController

class IosAppViewModel : AppViewModel(KVault()) {
    override val cacheManager: CacheManager
        get() = TODO("Not yet implemented")
    override val permissionsController: PermissionsController
        get() = TODO("Not yet implemented")
    override val locationTracker: LocationTracker
        get() = TODO("Not yet implemented")
    override val platformContext: PlatformContext
        get() = TODO("Not yet implemented")
}