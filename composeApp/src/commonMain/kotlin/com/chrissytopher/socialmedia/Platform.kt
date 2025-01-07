package com.chrissytopher.socialmedia

import com.liftric.kvault.KVault

abstract class Platform {
    abstract val name: String
    open val apiClient = ServerApi()
    abstract val kvault: KVault
}

const val USERNAME_KEY = "USERNAME"
const val EMAIL_KEY = "EMAIL"
const val ACCOUNT_KEYPAIR_KEY = "ACCOUNT_KEYPAIR"
const val ACCOUNT_CERTIFICATE_KEY = "ACCOUNT_CERTIFICATE"