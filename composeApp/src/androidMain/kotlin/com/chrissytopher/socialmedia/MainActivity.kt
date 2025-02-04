package com.chrissytopher.socialmedia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import dev.icerock.moko.permissions.PermissionsController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val platform = AndroidPlatform(this)
        val authManager = AuthenticationManager(platform)
        val permissionsController = PermissionsController(this)
        permissionsController.bind(this)
        setContent {
            CompositionLocalProvider(LocalPlatform provides platform) {
                CompositionLocalProvider(LocalAuthenticationManager provides authManager) {
                    CompositionLocalProvider(LocalPermissionsController provides permissionsController) {
                        App()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    MaterialTheme {
        LoginPreview()
    }
}