package com.chrissytopher.socialmedia

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntSize
import com.liftric.kvault.KVault

abstract class Platform {
    abstract val name: String
    open val apiClient = ServerApi()
    abstract val kvault: KVault

    abstract fun livingInFearOfBackGestures(): Boolean

    @Composable
    abstract fun BackHandler(enabled: Boolean, onBack: () -> Unit)
}

const val USERNAME_KEY = "USERNAME"
const val EMAIL_KEY = "EMAIL"
const val ACCOUNT_KEYPAIR_KEY = "ACCOUNT_KEYPAIR"
const val ACCOUNT_CERTIFICATE_KEY = "ACCOUNT_CERTIFICATE"

@Composable
expect fun getScreenSize(): IntSize