package com.chrissytopher.socialmedia

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNames

const val CREATE_ACCOUNT_LAMBDA_URL = "https://7vdsxbmbhjc4csljqcvu3bw4ou0gupnr.lambda-url.us-west-2.on.aws/"
const val AUTH_SERVER_PUBLIC_KEY_URL = "https://social-media-account-provisioning-public-key.s3.us-west-2.amazonaws.com/server_public_key.der"

class ServerApi(private val httpClient: HttpClient = HttpClient()) {

    suspend fun greet(): String {
        return httpClient.get("$SERVER_ADDRESS/").bodyAsText()
    }

    suspend fun createAccount(req: CreateAccountRequest): CreateAccountResponse? = runCatching {
        println(Json.encodeToString(req))
        val res = httpClient.post(CREATE_ACCOUNT_LAMBDA_URL) {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(req))
        }.bodyAsText()
        println(res)
        return Json.decodeFromString(res)
    }.getOrThrow()

    suspend fun authServerPublicKey(): String? = runCatching {
        return httpClient.get(AUTH_SERVER_PUBLIC_KEY_URL).bodyAsText()
    }.getOrThrow()
}
@Serializable
data class CreateAccountRequest(
    var username: String,
    var email: String,
    var csr: String,
)

@Serializable
data class CreateAccountResponse(
    @SerialName("error_code")
    val errorCode: Int,
    @SerialName("error_message")
    val errorMessage: String?,
    val certificate: String?,
)