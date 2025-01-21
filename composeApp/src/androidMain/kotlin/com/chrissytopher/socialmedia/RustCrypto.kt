package com.chrissytopher.socialmedia

object RustCrypto {
    init {
        System.loadLibrary("rust_crypto")
    }

    external fun newKeypair(): ByteArray
    external fun createCsr(keypair: ByteArray): ByteArray
    external fun verifyAccountCertificate(keypair: ByteArray, username: String, certificateBase64: String, serverPublicKey: String): Boolean
}