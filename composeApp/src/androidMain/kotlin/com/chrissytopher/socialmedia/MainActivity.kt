package com.chrissytopher.socialmedia

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.io.Source
import kotlinx.io.asSource
import kotlinx.io.buffered

class MainActivity : ComponentActivity() {
    companion object{
        var pickedImages: (List<Source>) -> Unit = {}
    }

    val imagePicker = registerForActivityResult(PickMultipleVisualMedia()) { uris: List<Uri> ->
        pickedImages(uris.mapNotNull {
            contentResolver.openInputStream(it)?.asSource()?.buffered()
        })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val platform = AndroidPlatform(this)
        val permissionsController = PermissionsController(this)
        permissionsController.bind(this)
        val locationTracker = LocationTracker(permissionsController)
        locationTracker.bind(this)
        val viewModel: AndroidAppViewModel by viewModels { AndroidAppViewModel.factory(this.applicationContext, permissionsController, locationTracker) }
        setContent {
            CompositionLocalProvider(LocalPlatform provides platform) {
                App(viewModel)
            }
        }
    }
}