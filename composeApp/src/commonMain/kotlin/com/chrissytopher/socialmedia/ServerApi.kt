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
const val ATTRIBUTES_LAMBDA_URL = "https://7utmgtt5ck5pgt2tljhyeqtssa0fmvig.lambda-url.us-west-2.on.aws"
const val ICON_UPLOAD_LAMBDA_URL = "https://tn3z6kwjpinbbvpkkxvpy5w6v40zrcbs.lambda-url.us-east-2.on.aws"

class ServerApi(val httpClient: HttpClient = HttpClient(), private val authenticationManager: AuthenticationManager) {
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
    suspend fun uploadIconMedia(data:ByteArray){
        val getRes = httpClient.post("$ICON_UPLOAD_LAMBDA_URL/post-icon") {
            authenticationManager.addAuthHeaders(this)
        }.bodyAsText()
        val json = Json.decodeFromString<JsonObject>(getRes)
        val uploadUrl = json["url"]!!.jsonPrimitive.content
        val uploadRes = httpClient.put(uploadUrl) {
            setBody(data)
        }
    }

    suspend fun uploadIconInfo(info: JsonObject): Result<Unit> = runCatching {
        val res = httpClient.post("$ICON_UPLOAD_LAMBDA_URL/post-icon-info") {
            setBody(info)
            contentType(ContentType.Application.Json)
            authenticationManager.addAuthHeaders(this)
        }
        if (!res.status.isSuccess()) throw Exception("non-200 status: ${res.status} (${res.bodyAsText()})")
    }
    suspend fun getIconUrl(givenUsername: String): Result<String> = runCatching {
        val res = httpClient.post("$ICON_UPLOAD_LAMBDA_URL/get-icon?username=$givenUsername") {
            authenticationManager.addAuthHeaders(this)
        }
        println(res.bodyAsText())
        return@runCatching res.bodyAsText()
    }
    suspend fun getIconInfo(givenUsername: String): Result<JsonObject> = runCatching {
        val res = httpClient.post("$ICON_UPLOAD_LAMBDA_URL/get-icon-info?username=$givenUsername") {
            authenticationManager.addAuthHeaders(this)
        }
        println("iconInfo is"+res.bodyAsText())
        return@runCatching Json.decodeFromString(res.bodyAsText())
    }

    suspend fun getRecommendations(location: LatLng): Result<List<String>> = runCatching {
        val res = httpClient.post("$POST_UPLOAD_LAMBDA_URL/recommendations?location=${locationFormatted(location)}&sort_by=location") {
            authenticationManager.addAuthHeaders(this)
        }
        return@runCatching Json.decodeFromString(res.bodyAsText())
    }

    suspend fun getLikes(postId: String): Result<List<Attribute>> = runCatching {
        val res = httpClient.get("$ATTRIBUTES_LAMBDA_URL/get-likes?post-id=$postId") {
            authenticationManager.addAuthHeaders(this)
        }
        return@runCatching Json.decodeFromString(res.bodyAsText())
    }

    suspend fun setAttribute(postId: String, attributeType: String, value: String): Result<Unit> = runCatching {
        val res = println("set attribute: " + httpClient.post("$ATTRIBUTES_LAMBDA_URL/set-attribute") {
            contentType(ContentType.Application.Json)
            setBody(Attribute("", postId, attributeType, authenticationManager.username!!, value))
            authenticationManager.addAuthHeaders(this)
        }.bodyAsText())
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

@Serializable
data class Attribute(
    val id: String,
    @SerialName("post_id")
    val postId: String,
    @SerialName("attribute_type")
    val attributeType: String,
    val user: String,
    val value: String,
)

fun <T> Result<T>.getOrNullAndThrow(message: String = ""): T? {
    exceptionOrNull()?.let {
        if (message.isNotEmpty()) {
            println("$message:")
        }
        it.printStackTrace()
    }
    return getOrNull()
}