package com.chrissytopher.socialmedia

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

class ServerApi {
    private val httpClient = HttpClient()

    suspend fun greet(): String {
        return httpClient.get("$SERVER_ADDRESS/").bodyAsText()
    }
}