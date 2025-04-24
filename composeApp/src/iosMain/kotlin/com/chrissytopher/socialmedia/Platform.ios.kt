package com.chrissytopher.socialmedia

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntSize
import com.chrissytopher.socialmedia.theme.AppTypography
import com.chrissytopher.socialmedia.theme.darkScheme
import com.chrissytopher.socialmedia.theme.lightScheme
import kotlinx.coroutines.channels.Channel
import kotlinx.io.Source
import platform.UIKit.UIDevice

class IOSPlatform(private var launchPhotoPicker: () -> Unit): Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override fun livingInFearOfBackGestures(): Boolean {
        return false
    }

    @Composable
    override fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
        //RIP ios
    }

    @Composable
    override fun AppTheme(
        darkTheme: Boolean?,
        dynamicColor: Boolean,
        content: @Composable (() -> Unit)
    ) {
        val colorScheme = when {
            darkTheme ?: isSystemInDarkTheme() -> darkScheme
            else -> lightScheme
        }

        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }

    override suspend fun pickImages(): List<Source> {
        val channel = Channel<List<Source>>()
        imagePickerCallback = {
            channel.send(it)
        }
        launchPhotoPicker()
        return channel.receive()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenSize(): IntSize {
    return LocalWindowInfo.current.containerSize
}
