package com.chrissytopher.socialmedia

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

const val CREATE_ACCOUNT_LAMBDA_URL = "https://7vdsxbmbhjc4csljqcvu3bw4ou0gupnr.lambda-url.us-west-2.on.aws/"
const val POST_UPLOAD_LAMBDA_URL = "https://vby32pkmko4kmlvjk4oq6othue0hkarn.lambda-url.us-west-2.on.aws"
const val AUTH_SERVER_PUBLIC_KEY_URL = "https://social-media-account-provisioning-public-key.s3.us-west-2.amazonaws.com/server_public_key.der"

class ServerApi(private val httpClient: HttpClient = HttpClient(), private val authenticationManager: AuthenticationManager) {

    suspend fun greet(): String {
        return httpClient.get("$SERVER_ADDRESS/").bodyAsText()
    }

    suspend fun createAccount(req: CreateAccountRequest): CreateAccountResponse? = runCatching {
        println(Json.encodeToString(req))
        val res = httpClient.post(CREATE_ACCOUNT_LAMBDA_URL) {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(req))
        }.bodyAsText()
        return Json.decodeFromString(res)
    }.getOrThrow()

    suspend fun authServerPublicKey(): String? = runCatching {
        return httpClient.get(AUTH_SERVER_PUBLIC_KEY_URL).bodyAsText()
    }.getOrThrow()

    suspend fun uploadPostMedia(data: ByteArray): String? = runCatching {
        val res = httpClient.post("$POST_UPLOAD_LAMBDA_URL/media") {
            setBody(data)
            authenticationManager.addAuthHeaders(this)
        }.bodyAsText()
        return Json.decodeFromString<JsonObject>(res)["content_id"]?.jsonPrimitive?.contentOrNull
    }.getOrThrow()

    suspend fun uploadPostInfo(info: String): Result<Unit> = runCatching {
        val res = httpClient.post("$POST_UPLOAD_LAMBDA_URL/info") {
            setBody(info)
            contentType(ContentType.Application.Json)
            authenticationManager.addAuthHeaders(this)
        }
        if (!res.status.isSuccess()) throw Exception("non-200 status: ${res.status} (${res.bodyAsText()})")
    }
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