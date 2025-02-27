package com.chrissytopher.socialmedia

import io.ktor.client.request.HttpRequestBuilder
import kotlinx.datetime.Clock
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AuthenticationManager(private val viewModel: AppViewModel) {
    fun loggedIn(): Boolean {
        return viewModel.kvault.existsObject(USERNAME_KEY)
            && viewModel.kvault.existsObject(EMAIL_KEY)
            && viewModel.kvault.existsObject(ACCOUNT_KEYPAIR_KEY)
            && viewModel.kvault.existsObject(ACCOUNT_CERTIFICATE_KEY)
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun createAccount(username: String, email: String): String? {
        val keypair = newKeypair()
        val csrDer = createCsr(keypair)
        val reqBody = CreateAccountRequest(username, email, Base64.encode(csrDer))
        val res = viewModel.apiClient.createAccount(reqBody).getOrNullAndThrow() ?: return "Request failed, try again"
        if (res.errorCode != 0 || res.errorMessage != null || res.certificate == null) {
            return res.errorMessage?.let { "Something went wrong: $it (${res.errorCode})" } ?: "An unknown error occurred"
        }
        val serverPublicKey = viewModel.apiClient.authServerPublicKey().getOrNullAndThrow() ?: return "Something went wrong, try again later"
        val verified = verifyAccountCertificate(keypair, username, res.certificate, serverPublicKey)
        if (!verified) {
            return "Something went wrong, try again"
        }
        viewModel.kvault.set(USERNAME_KEY, username)
        viewModel.kvault.set(EMAIL_KEY, email)
        viewModel.kvault.set(ACCOUNT_KEYPAIR_KEY, keypair.serialize())
        viewModel.kvault.set(ACCOUNT_CERTIFICATE_KEY, Base64.decode(res.certificate))
        return null
    }
    val email: String?
        get() = viewModel.kvault.string(EMAIL_KEY)
    val username: String?
        get() = viewModel.kvault.string(USERNAME_KEY)
    val certificate: ByteArray?
        get() = viewModel.kvault.data(ACCOUNT_CERTIFICATE_KEY)
    val keypair: ByteArray?
        get() = viewModel.kvault.data(ACCOUNT_KEYPAIR_KEY)

    @OptIn(ExperimentalEncodingApi::class)
    fun addAuthHeaders(req: HttpRequestBuilder) {
        req.apply {
            username?.let { username ->
                headers["X-Username"] = username
                keypair?.let { keypair ->
                    val nonce = Clock.System.now().toEpochMilliseconds().toString()
                    //TODO: put some random bytes at the end
                    headers["X-Nonce"] = nonce
                    headers["X-Auth-Signature"] = accountSignature(keypair, username, nonce)
                }
            }
            certificate?.let { headers["X-Auth-Cert"] = Base64.encode(it) }

        }
    }
}