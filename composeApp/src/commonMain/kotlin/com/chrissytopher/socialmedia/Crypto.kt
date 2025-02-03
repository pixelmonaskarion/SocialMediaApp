package com.chrissytopher.socialmedia

interface Keypair {
    fun serialize(): ByteArray
    fun deserialize(bytes: ByteArray)
}

expect fun newKeypair(): Keypair

expect fun createCsr(keypair: Keypair): ByteArray

expect fun verifyAccountCertificate(keypair: Keypair, username: String, certificateBase64: String, serverPublicKey: String): Boolean

expect fun accountSignature(keypair: ByteArray, username: String, nonce: String): String