package com.chrissytopher.socialmedia

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import socialmediaapp.composeapp.generated.resources.Res
import socialmediaapp.composeapp.generated.resources.compose_multiplatform

val LocalPlatform: ProvidableCompositionLocal<Platform> = compositionLocalOf { error("no platform provided") }

@Composable
@Preview
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { showContent = !showContent }) {
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
                    Text("Compose: $greeting")
                }
            }
        }
    }
}