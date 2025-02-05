package com.chrissytopher.socialmedia

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntSize
import com.liftric.kvault.KVault
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.io.Source

abstract class Platform {
    abstract val name: String
    open val authenticationManager = AuthenticationManager(this)
    open val apiClient = ServerApi(authenticationManager = authenticationManager)
    abstract val kvault: KVault

    abstract fun livingInFearOfBackGestures(): Boolean

    @Composable
    abstract fun BackHandler(enabled: Boolean, onBack: () -> Unit)

    abstract fun getLocationTracker(permissionsController: PermissionsController): LocationTracker

    abstract suspend fun pickImages(): List<Source>
}

const val USERNAME_KEY = "USERNAME"
const val EMAIL_KEY = "EMAIL"
const val ACCOUNT_KEYPAIR_KEY = "ACCOUNT_KEYPAIR"
const val ACCOUNT_CERTIFICATE_KEY = "ACCOUNT_CERTIFICATE"

@Composable
expect fun getScreenSize(): IntSize