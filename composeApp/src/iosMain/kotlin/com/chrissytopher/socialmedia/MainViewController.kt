package com.chrissytopher.socialmedia

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController

val platform = IOSPlatform()

fun MainViewController() = ComposeUIViewController {
    CompositionLocalProvider(LocalPlatform provides platform) {
        App()
    }
}