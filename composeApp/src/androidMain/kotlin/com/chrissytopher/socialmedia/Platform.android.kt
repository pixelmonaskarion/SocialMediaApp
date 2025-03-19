package com.chrissytopher.socialmedia

import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.chrissytopher.socialmedia.theme.AppTypography
import com.chrissytopher.socialmedia.theme.darkScheme
import com.chrissytopher.socialmedia.theme.lightScheme
import com.liftric.kvault.KVault
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.PermissionsController
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.io.Source
import kotlin.coroutines.coroutineContext

class AndroidPlatform(private val mainActivity: MainActivity) : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"

    override fun livingInFearOfBackGestures(): Boolean {
        //https://www.b4x.com/android/forum/threads/solved-how-to-determine-users-system-navigation-mode-back-button-or-side-swipe.159347/
        val navigationMode = Settings.Secure.getInt(mainActivity.contentResolver, "navigation_mode")
        return (navigationMode == 2)
    }

    @Composable
    override fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
        androidx.activity.compose.BackHandler(enabled, onBack)
    }

    override suspend fun pickImages(): List<Source> {
        val channel = Channel<List<Source>>()
        MainActivity.pickedImages = {
            CoroutineScope(Dispatchers.IO).launch { channel.send(it) }
        }
        mainActivity.imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        return channel.receive()
    }

    @Composable
    override fun AppTheme(
        darkTheme: Boolean?,
        // Dynamic color is available on Android 12+
        dynamicColor: Boolean,
        content: @Composable () -> Unit
    ) {
        val colorScheme = when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme ?: isSystemInDarkTheme()) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme ?: isSystemInDarkTheme() -> darkScheme
            else -> lightScheme
        }

        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

@Composable
actual fun getScreenSize(): IntSize {
    return with(LocalDensity.current) {
        IntSize(LocalConfiguration.current.screenWidthDp.dp.roundToPx(), LocalConfiguration.current.screenHeightDp.dp.roundToPx())
    }
}