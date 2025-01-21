package com.chrissytopher.socialmedia

import kotlin.io.encoding.Base64

interface Keypair {
    fun serialize(): ByteArray
    fun deserialize(bytes: ByteArray)
}

expect fun newKeypair(): Keypair

expect fun createCsr(keypair: Keypair): ByteArray

expect fun verifyAccountCertificate(keypair: Keypair, username: String, certificateBase64: String, serverPublicKey: String): Boolean