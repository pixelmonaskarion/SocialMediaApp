package com.chrissytopher.socialmedia

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import androidx.lifecycle.viewmodel.compose.viewModel

val platform = IOSPlatform()

fun MainViewController() = ComposeUIViewController {
    val viewModel = viewModel { IosAppViewModel() }
    CompositionLocalProvider(LocalPlatform provides platform) {
        App(viewModel)
    }
}