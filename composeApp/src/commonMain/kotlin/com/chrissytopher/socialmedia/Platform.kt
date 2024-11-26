package com.chrissytopher.socialmedia

abstract class Platform {
    abstract val name: String
    val apiClient = ServerApi()
}