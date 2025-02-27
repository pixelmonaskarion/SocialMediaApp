package com.chrissytopher.socialmedia

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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

@Composable
fun AccountSettingScreen(viewModel: AppViewModel) {
    Column(Modifier.padding(10.dp)) {
        val platform = LocalPlatform.current
        val coroutineScope = rememberCoroutineScope()
        var username: String? by remember { mutableStateOf(null)}
        var email: String? by remember {mutableStateOf(null)}
        if (viewModel.authenticationManager.loggedIn()){
            username = viewModel.authenticationManager.username
            email = viewModel.authenticationManager.email
        }
        username?.let { Text(it, style = MaterialTheme.typography.titleLarge) }
        email?.let { Text(it, style = MaterialTheme.typography.titleLarge)}

}

}