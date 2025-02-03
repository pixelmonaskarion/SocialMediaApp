package com.chrissytopher.socialmedia

import java.nio.ByteBuffer
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AndroidKeypair(var public: ByteArray, var private: ByteArray) : Keypair {
    override fun serialize(): ByteArray {
        val bytes = ByteBuffer.allocate(Int.SIZE_BYTES).putInt(public.size).array().toMutableList()
        bytes.addAll(public.toMutableList())
        bytes.addAll(private.toMutableList())
        return bytes.toByteArray()
    }

    override fun deserialize(bytes: ByteArray) {
        val publicLength = ByteBuffer.wrap(bytes.take(Int.SIZE_BYTES).toByteArray()).int
        val bytesList = bytes.toMutableList()
        val publicDer = bytesList.slice(Int.SIZE_BYTES until (publicLength+Int.SIZE_BYTES))
        val privateDer = bytesList.slice((publicLength+Int.SIZE_BYTES) until bytesList.size)
        this.public = publicDer.toByteArray()
        this.private = privateDer.toByteArray()
    }

    constructor() : this(ByteArray(0), ByteArray(0))
}

actual fun newKeypair(): Keypair {
    val serialized = RustCrypto.newKeypair()
    val keypair = AndroidKeypair()
    keypair.deserialize(serialized)
    return keypair
}

actual fun createCsr(keypair: Keypair): ByteArray {
    return RustCrypto.createCsr(keypair.serialize())
}

actual fun verifyAccountCertificate(keypair: Keypair, username: String, certificateBase64: String, serverPublicKey: String): Boolean {
    return RustCrypto.verifyAccountCertificate(keypair.serialize(), username, certificateBase64, serverPublicKey)
}

actual fun accountSignature(keypair: ByteArray, username: String, nonce: String): String {
    return RustCrypto.accountSignature(keypair, username, nonce)
}