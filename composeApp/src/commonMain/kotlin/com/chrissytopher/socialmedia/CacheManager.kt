package com.chrissytopher.socialmedia

import kotlinx.io.files.Path
import kotlinx.serialization.json.JsonObject

class CacheManager(private val platform: Platform, private val cacheDirectory: Path) {
    fun getCachedPostInfo(contentId: String): JsonObject? {

    }

//    fun cacheInfo(contentId: String)
}