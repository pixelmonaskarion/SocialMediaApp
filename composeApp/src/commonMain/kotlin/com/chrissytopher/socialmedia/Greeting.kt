package com.chrissytopher.socialmedia

suspend fun Platform.greet(): String {
    return apiClient.greet()
}
