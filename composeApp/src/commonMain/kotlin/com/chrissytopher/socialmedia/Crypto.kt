package com.chrissytopher.socialmedia

interface Keypair {
    fun serialize(): ByteArray
}

expect fun newKeypair(): Keypair

expect fun createCsr(keypair: Keypair): ByteArray

expect fun verifyAccountCertificate(keypair: Keypair, username: String, certificate: ByteArray, serverPublicKey: ByteArray): Boolean