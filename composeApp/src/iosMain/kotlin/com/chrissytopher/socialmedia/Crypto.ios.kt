package com.chrissytopher.socialmedia

import rustCrypto.*

actual fun newKeypair(): Keypair {
    new_keypair()
}

actual fun createCsr(keypair: Keypair): ByteArray {
    TODO()
}

actual fun verifyAccountCertificate(keypair: Keypair, username: String, certificateBase64: String, serverPublicKey: String): Boolean {
    TODO()
}

actual fun accountSignature(keypair: ByteArray, username: String, nonce: String): String {
    TODO()
}