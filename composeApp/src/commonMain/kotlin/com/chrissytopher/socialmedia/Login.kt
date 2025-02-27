package com.chrissytopher.socialmedia

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun Login(done: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error: String? by remember { mutableStateOf(null) }
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Create an Account", style = MaterialTheme.typography.headlineLarge)
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.animateContentSize()) {
            OutlinedTextField(username, onValueChange = { username = it }, label = { Text("Username") }, singleLine = true, keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.None, autoCorrectEnabled = false, keyboardType = KeyboardType.Password, imeAction = ImeAction.Next))
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(email, onValueChange = { email = it }, label = { Text("Email") }, supportingText = { error?.let { Text(it) } }, isError = error != null, singleLine = true, keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.None, autoCorrectEnabled = false, keyboardType = KeyboardType.Email, imeAction = ImeAction.Next))
            Spacer(Modifier.height(15.dp))
            val authManager = LocalAuthenticationManager.current
            val coroutineScope = rememberCoroutineScope()
            var normalSize by remember { mutableStateOf(IntSize.Zero) }
            Button(enabled = username.isNotEmpty() && email.isNotEmpty() && !loading, onClick = {
                loading = true
                coroutineScope.launch {
                    error = authManager.createAccount(username, email)
                    if (error == null) {
                        done()
                    }
                    loading = false
                }
            }) {
                Box {
                    androidx.compose.animation.AnimatedVisibility(loading, enter = fadeIn(), exit = fadeOut()) {
                        Box(Modifier.size(with(LocalDensity.current) { normalSize.toSize().toDpSize() })) {
                            CircularProgressIndicator(color = ButtonDefaults.buttonColors().contentColor, modifier = Modifier.align(Alignment.Center).size(with(LocalDensity.current) { min(normalSize.width.toDp(), normalSize.height.toDp()) }))
                        }
                    }
                    androidx.compose.animation.AnimatedVisibility(!loading, enter = fadeIn(), exit = fadeOut()) {
                        Text("Continue", modifier = Modifier.onSizeChanged { if (!loading) normalSize = it })
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun LoginPreview() {
    Surface {
        Login() {}
    }
}