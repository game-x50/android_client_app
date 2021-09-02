package com.ruslan.hlushan.android.storage.encryption

import android.content.Context
import android.util.Base64
import com.google.crypto.tink.DeterministicAead
import com.ironz.binaryprefs.encryption.KeyEncryption
import com.ruslan.hlushan.android.extensions.signatureSha

internal class TinkKeyEncryption(context: Context, private val aead: DeterministicAead) : KeyEncryption {

    private val signature = context.signatureSha
    private val encoderFlags = (Base64.NO_WRAP or Base64.URL_SAFE)

    override fun encrypt(plaintext: String): String {
        val cipherText = aead.encryptDeterministically(plaintext.toByteArray(), signature)
        return Base64.encodeToString(cipherText, encoderFlags)
    }

    override fun decrypt(cipher: String): String {
        val ciperText = Base64.decode(cipher, encoderFlags)
        return String(aead.decryptDeterministically(ciperText, signature))
    }
}