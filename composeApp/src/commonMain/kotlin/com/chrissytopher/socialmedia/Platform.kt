package com.chrissytopher.socialmedia

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntSize
import com.liftric.kvault.KVault
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.io.Source

interface Platform {
    val name: String

    fun livingInFearOfBackGestures(): Boolean

    @Composable
    fun BackHandler(enabled: Boolean, onBack: () -> Unit)

    suspend fun pickImages(): List<Source>
}

@Composable
expect fun getScreenSize(): IntSize