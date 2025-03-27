package com.chrissytopher.socialmedia

import kotlinx.coroutines.CoroutineScope
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class CacheManager(private val cacheDirectory: Path, val coroutineScope: CoroutineScope) {
    fun getCachedPostInfo(contentId: String): JsonObject? = runCatching<JsonObject> {
        return@runCatching Json.decodeFromString(SystemFileSystem.source(fileForInfo(contentId)).buffered().readByteArray().decodeToString())
    }.getOrNull()

    fun getCachedPostMedia(contentId: String): ByteArray? = runCatching<ByteArray> {
        return@runCatching SystemFileSystem.source(fileForMedia(contentId)).buffered().readByteArray()
    }.getOrNull()

    fun cacheInfo(contentId: String, info: JsonObject) {
        val infoFile = fileForInfo(contentId)
        infoFile.parent?.let { SystemFileSystem.createDirectories(it) }
        val bufferedSource = SystemFileSystem.sink(infoFile).buffered()
        bufferedSource.write(Json.encodeToString(info).encodeToByteArray())
    }

    fun cacheMedia(contentId: String, media: ByteArray) {
        val mediaFile = fileForMedia(contentId)
        mediaFile.parent?.let { SystemFileSystem.createDirectories(it) }
        val bufferedSource = SystemFileSystem.sink(mediaFile).buffered()
        bufferedSource.write(media)
    }

    private fun fileForInfo(contentId: String): Path = Path(cacheDirectory, "info", "$contentId.json")
    private fun fileForMedia(contentId: String): Path = Path(cacheDirectory, "media", "$contentId.json")
}