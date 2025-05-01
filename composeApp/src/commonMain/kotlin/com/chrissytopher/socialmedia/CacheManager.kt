package com.chrissytopher.socialmedia

import androidx.compose.ui.graphics.painter.Painter
import coil3.ImageLoader
import coil3.compose.asPainter
import coil3.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class CacheManager(private val cacheDirectory: Path, val coroutineScope: CoroutineScope) {
    init {
        SystemFileSystem.createDirectories(cacheDirectory)
    }
    fun getCachedPostInfo(contentId: String): JsonObject? = runCatching<JsonObject> {
        return@runCatching Json.decodeFromString(SystemFileSystem.source(fileForInfo(contentId)).buffered().readAndClose().decodeToString())
    }.getOrNull()

    fun getCachedPostMedia(contentId: String): ByteArray? = runCatching<ByteArray> {
        return@runCatching SystemFileSystem.source(fileForMedia(contentId)).buffered().readAndClose()
    }.getOrNull()

    fun cacheInfo(contentId: String, info: JsonObject) {
        val infoFile = fileForInfo(contentId)
        infoFile.parent?.let { SystemFileSystem.createDirectories(it) }
        val bufferedSource = SystemFileSystem.sink(infoFile).buffered()
        bufferedSource.write(Json.encodeToString(info).encodeToByteArray())
        bufferedSource.close()
    }

    fun cacheMedia(contentId: String, media: ByteArray) {
        val mediaFile = fileForMedia(contentId)
        mediaFile.parent?.let { SystemFileSystem.createDirectories(it) }
        val bufferedSource = SystemFileSystem.sink(mediaFile).buffered()
        bufferedSource.write(media)
        bufferedSource.close()
    }

    private fun fileForInfo(contentId: String): Path = Path(cacheDirectory, "info", "$contentId.json")
    private fun fileForMedia(contentId: String): Path = Path(cacheDirectory, "media", "$contentId.json")
}

fun Source.readAndClose(): ByteArray {
    val bytes = readByteArray()
    close()
    return bytes
}