package com.chrissytopher.socialmedia

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntSize
import kotlinx.io.Source

interface Platform {
    val name: String

    fun livingInFearOfBackGestures(): Boolean

    @Composable
    fun BackHandler(enabled: Boolean, onBack: () -> Unit)

    @Composable
    fun AppTheme(
        darkTheme: Boolean?,
        // Dynamic color is available on Android 12+
        dynamicColor: Boolean = true,
        content: @Composable () -> Unit
    )


    suspend fun pickImages(): List<Source>
}

@Composable
expect fun getScreenSize(): IntSize