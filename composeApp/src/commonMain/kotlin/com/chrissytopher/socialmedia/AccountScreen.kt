package com.chrissytopher.socialmedia

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter

@Composable
fun AccountSettingScreen() {
    val platform = LocalPlatform.current
    val authManager = LocalAuthenticationManager.current
    val coroutineScope = rememberCoroutineScope()
    var username: String? by remember { mutableStateOf(null)}
    var email: String? by remember {mutableStateOf(null)}
    var profilePicture: String? by remember { mutableStateOf(null) }
    if (authManager.loggedIn()){
        username = authManager.username
        email = authManager.email
    }
    // I would put the image fetch request here
    Column(Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.size(10.dp))
        val painter = rememberAsyncImagePainter(model = "https://upload.wikimedia.org/wikipedia/commons/a/a9/Grace_Abbott_1929.jpg")
        Image(
            painter = painter,
            contentDescription = "User profile picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer {
                    clip = true
                    shape = RoundedCornerShape(percent = 50)


                }

        )
        username?.let { Text(it, style = MaterialTheme.typography.titleLarge) }
        email?.let { Text(it, style = MaterialTheme.typography.titleLarge)}

}

}