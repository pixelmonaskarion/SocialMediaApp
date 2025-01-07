package com.chrissytopher.socialmedia

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

const val CREATE_ACCOUNT_LAMBDA_URL = ""
const val AUTH_SERVER_PUBLIC_KEY_URL = "https://social-media-account-provisioning-public-key.s3.us-west-2.amazonaws.com/server_public_key.der"

class ServerApi(private val httpClient: HttpClient = HttpClient()) {

    suspend fun greet(): String {
        return httpClient.get("$SERVER_ADDRESS/").bodyAsText()
    }

    suspend fun createAccount(req: CreateAccountRequest): CreateAccountResponse? = runCatching {
        return Json.decodeFromString(httpClient.post(CREATE_ACCOUNT_LAMBDA_URL) {
            contentType(ContentType.Application.Json)
            setBody(req)
        }.bodyAsText())
    }.getOrNull()

    suspend fun authServerPublicKey(): ByteArray? = runCatching {
        return httpClient.get(AUTH_SERVER_PUBLIC_KEY_URL).bodyAsBytes()
    }.getOrNull()
}
@Serializable
data class CreateAccountRequest(
    var username: String,
    var email: String,
    var csrBytes: String,
)

@Serializable
data class CreateAccountResponse(
    val errorCode: Int,
    val errorMessage: String?,
    val certificate: String?,
)