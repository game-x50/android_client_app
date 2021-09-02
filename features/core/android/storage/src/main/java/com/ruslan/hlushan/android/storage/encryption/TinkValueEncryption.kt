package com.ruslan.hlushan.android.storage.encryption

import android.content.Context
import android.util.Base64
import com.google.crypto.tink.Aead
import com.ironz.binaryprefs.encryption.ValueEncryption
import com.ruslan.hlushan.android.extensions.signatureSha

private const val COUNT_LAST_BYTES_OF_SIGNATURE_SHA = 16

internal class TinkValueEncryption(context: Context, private val aead: Aead) : ValueEncryption {

    private val signature: ByteArray = context.signatureSha
            .takeLast(COUNT_LAST_BYTES_OF_SIGNATURE_SHA)
            .toByteArray()

    override fun encrypt(plaintext: ByteArray): ByteArray {
        val cipherText = aead.encrypt(plaintext, signature)
        return Base64.encode(cipherText, Base64.DEFAULT)
    }

    override fun decrypt(cipher: ByteArray): ByteArray {
        val cipherText = Base64.decode(cipher, Base64.DEFAULT)
        return aead.decrypt(cipherText, signature)
    }
}