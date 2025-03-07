package com.chrissytopher.socialmedia

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntSize
import kotlinx.io.Source
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override fun livingInFearOfBackGestures(): Boolean {
        return false
    }

    @Composable
    override fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
        //RIP ios
    }

    override suspend fun pickImages(): List<Source> {
        TODO("Not yet implemented")
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenSize(): IntSize {
    return LocalWindowInfo.current.containerSize
}
