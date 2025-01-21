package com.chrissytopher.socialmedia

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AuthenticationManager(private val platform: Platform) {
    fun loggedIn(): Boolean {
        return platform.kvault.existsObject(USERNAME_KEY)
            && platform.kvault.existsObject(EMAIL_KEY)
            && platform.kvault.existsObject(ACCOUNT_KEYPAIR_KEY)
            && platform.kvault.existsObject(ACCOUNT_CERTIFICATE_KEY)
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun createAccount(username: String, email: String): String? {
        val keypair = newKeypair()
        val csrDer = createCsr(keypair)
        val reqBody = CreateAccountRequest(username, email, Base64.encode(csrDer))
        val res = platform.apiClient.createAccount(reqBody) ?: return "Request failed, try again"
        if (res.errorCode != 0 || res.errorMessage != null || res.certificate == null) {
            return res.errorMessage?.let { "Something went wrong: $it (${res.errorCode})" } ?: "An unknown error occurred"
        }
        val serverPublicKey = platform.apiClient.authServerPublicKey() ?: return "Something went wrong, try again later"
        val verified = verifyAccountCertificate(keypair, username, res.certificate, serverPublicKey)
        if (!verified) {
            return "Something went wrong, try again"
        }
        platform.kvault.set(USERNAME_KEY, username)
        platform.kvault.set(EMAIL_KEY, email)
        platform.kvault.set(ACCOUNT_KEYPAIR_KEY, keypair.serialize())
        platform.kvault.set(ACCOUNT_CERTIFICATE_KEY, Base64.decode(res.certificate))
        return null
    }
}