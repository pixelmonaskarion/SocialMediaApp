package com.chrissytopher.socialmedia

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import platform.Foundation.*
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
typealias NewKeypairLambda = () -> NSData
typealias CreateCsrLambda = (NSData) -> NSData
typealias VerifyAccountCertificateLambda = (NSData, String, String, String) -> Boolean
typealias AccountSignature = (NSData, String, String) -> String

var new_keypair: NewKeypairLambda? = null
var create_csr: CreateCsrLambda? = null
var verify_account_certificate: VerifyAccountCertificateLambda? = null
var account_signature: AccountSignature? = null

class IosKeypair(var public: ByteArray, var private: ByteArray) : Keypair {
    override fun serialize(): ByteArray {
        val bytes = Buffer()
        bytes.writeInt(public.size)
        bytes.write(public)
        bytes.write(private)
        return bytes.readByteArray()
    }

    override fun deserialize(bytes: ByteArray) {
        val buffer = Buffer().apply { write(bytes) }
        val publicLength = buffer.readInt()
        val publicDer = buffer.readByteArray(publicLength)
        val privateDer = buffer.readByteArray()
        this.public = publicDer
        this.private = privateDer
    }

    constructor() : this(ByteArray(0), ByteArray(0))
}

@OptIn(ExperimentalForeignApi::class)
actual fun newKeypair(): Keypair = IosKeypair().apply { deserialize(
    new_keypair!!().toByteArray()
) }

actual fun createCsr(keypair: Keypair): ByteArray = create_csr!!(keypair.serialize().toNSData()).toByteArray()

actual fun verifyAccountCertificate(keypair: Keypair, username: String, certificateBase64: String, serverPublicKey: String) = verify_account_certificate!!(keypair.serialize().toNSData(), username, certificateBase64, serverPublicKey)

actual fun accountSignature(keypair: ByteArray, username: String, nonce: String) = account_signature!!(keypair.toNSData(), username, nonce)

@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(): ByteArray {
    return ByteArray(length.toInt()).apply {
        usePinned {
            memcpy(it.addressOf(0), bytes, length)
        }
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun ByteArray.toNSData(): NSData {
    return memScoped {
        NSData.create(bytes = allocArrayOf(this@toNSData),
            length = this@toNSData.size.toULong())
    }
}