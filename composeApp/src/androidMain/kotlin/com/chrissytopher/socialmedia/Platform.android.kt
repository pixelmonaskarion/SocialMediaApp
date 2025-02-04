package com.chrissytopher.socialmedia

import android.content.Context
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.liftric.kvault.KVault
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.PermissionsController
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

class AndroidPlatform(private val context: Context): Platform() {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
    override val apiClient = ServerApi(
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json()
            }
        }, authenticationManager
    )
    override val kvault = KVault(context)

    override fun livingInFearOfBackGestures(): Boolean {
        //https://www.b4x.com/android/forum/threads/solved-how-to-determine-users-system-navigation-mode-back-button-or-side-swipe.159347/
        val navigationMode = Settings.Secure.getInt(context.contentResolver, "navigation_mode")
        return (navigationMode == 2)
    }

    @Composable
    override fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
        androidx.activity.compose.BackHandler(enabled, onBack)
    }

    override fun getLocationTracker(permissionsController: PermissionsController): LocationTracker {
        val tracker = LocationTracker(permissionsController)
        tracker.bind(context as ComponentActivity)
        return tracker
    }
}

@Composable
actual fun getScreenSize(): IntSize {
    return with(LocalDensity.current) {
        IntSize(LocalConfiguration.current.screenWidthDp.dp.roundToPx(), LocalConfiguration.current.screenHeightDp.dp.roundToPx())
    }
}