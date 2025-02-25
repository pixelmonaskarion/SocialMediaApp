package com.chrissytopher.socialmedia

import dev.icerock.moko.geo.LatLng
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
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

class ServerApi(val httpClient: HttpClient = HttpClient(), private val authenticationManager: AuthenticationManager) {

    suspend fun greet(): String {
        return httpClient.get("$SERVER_ADDRESS/").bodyAsText()
    }

    suspend fun createAccount(req: CreateAccountRequest): Result<CreateAccountResponse> = runCatching {
        println(Json.encodeToString(req))
        val res = httpClient.post(CREATE_ACCOUNT_LAMBDA_URL) {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(req))
        }.bodyAsText()
        return@runCatching Json.decodeFromString(res)
    }

    suspend fun authServerPublicKey(): Result<String> = runCatching {
        return@runCatching httpClient.get(AUTH_SERVER_PUBLIC_KEY_URL).bodyAsText()
    }

    suspend fun uploadPostMedia(data: ByteArray): Result<String> = runCatching {
        val getRes = httpClient.post("$POST_UPLOAD_LAMBDA_URL/post-media") {
            authenticationManager.addAuthHeaders(this)
        }.bodyAsText()
        val json = Json.decodeFromString<JsonObject>(getRes)
        val contentId = json["content_id"]!!.jsonPrimitive.content
        val uploadUrl = json["url"]!!.jsonPrimitive.content
        val uploadRes = httpClient.put(uploadUrl) {
            setBody(data)
        }

        return@runCatching contentId
    }

    suspend fun uploadPostInfo(info: String): Result<Unit> = runCatching {
        val res = httpClient.post("$POST_UPLOAD_LAMBDA_URL/post-info") {
            setBody(info)
            contentType(ContentType.Application.Json)
            authenticationManager.addAuthHeaders(this)
        }
        if (!res.status.isSuccess()) throw Exception("non-200 status: ${res.status} (${res.bodyAsText()})")
    }

    suspend fun getRecommendations(location: LatLng): Result<List<String>> = runCatching {
        val res = httpClient.post("$POST_UPLOAD_LAMBDA_URL/recommendations?location=${locationFormatted(location)}&sort_by=location") {
            authenticationManager.addAuthHeaders(this)
        }
        return@runCatching Json.decodeFromString(res.bodyAsText())
    }

    suspend fun getPostInfo(contentId: String): Result<JsonObject> = runCatching {
        val res = httpClient.post("$POST_UPLOAD_LAMBDA_URL/get-info?content_id=$contentId") {
            authenticationManager.addAuthHeaders(this)
        }
        return@runCatching Json.decodeFromString(res.bodyAsText())
    }

    suspend fun getPostMediaUrl(contentId: String): Result<String> = runCatching {
        val res = httpClient.post("$POST_UPLOAD_LAMBDA_URL/get-media?content_id=$contentId") {
            authenticationManager.addAuthHeaders(this)
        }
        return@runCatching res.bodyAsText()
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

fun <T> Result<T>.getOrNullAndThrow(message: String = ""): T? {
    exceptionOrNull()?.let {
        println("$message:")
        it.printStackTrace()
    }
    return getOrNull()
}