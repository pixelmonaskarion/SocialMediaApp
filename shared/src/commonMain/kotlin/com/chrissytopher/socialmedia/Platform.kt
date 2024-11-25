package com.chrissytopher.socialmedia

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform