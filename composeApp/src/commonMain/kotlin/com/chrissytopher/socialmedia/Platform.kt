package com.chrissytopher.socialmedia

abstract class Platform {
    abstract val name: String
    open val apiClient = ServerApi()
}