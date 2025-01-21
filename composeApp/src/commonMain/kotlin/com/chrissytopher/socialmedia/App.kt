package com.chrissytopher.socialmedia

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HeartBroken
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

val LocalPlatform: ProvidableCompositionLocal<Platform> = compositionLocalOf { error("no platform provided") }
val LocalAuthenticationManager: ProvidableCompositionLocal<AuthenticationManager> = compositionLocalOf { error("no authentication manager provided") }
@OptIn(ExperimentalUuidApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        val authenticationManager = LocalAuthenticationManager.current
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                showContent = !showContent
                coroutineScope.launch {
//                    val res = authenticationManager.createAccount(Uuid.random().toString(), "ble@gleep.io")
//                    println("auth result: $res")
                }
            }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                var greeting by remember { mutableStateOf("") }
                val platform = LocalPlatform.current
                LaunchedEffect(showContent) {
                    if (showContent) {
                        launch { runCatching {
                            greeting = platform.greet()
                        } }
                    }
                }
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRceH_ZcA_58UOeXRWeJetPDs9_ipgN6lKuHA&s", contentDescription = null)
                    Icon(Icons.Outlined.HeartBroken, null)
                    Text("Compose: $greeting")
                }
            }
        }
    }
}