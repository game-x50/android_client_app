package com.ruslan.hlushan.android.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.crypto.tink.Aead
import com.google.crypto.tink.DeterministicAead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.daead.DeterministicAeadConfig
import com.google.crypto.tink.hybrid.HybridConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.google.crypto.tink.integration.android.AndroidKeystoreKmsClient
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.ruslan.hlushan.android.storage.encryption.TinkKeyEncryption
import com.ruslan.hlushan.android.storage.encryption.TinkValueEncryption

//https://github.com/Fi5t/advanced-tink

object SharedPrefsProvider {

    fun init() {
        DeterministicAeadConfig.register()
        HybridConfig.register()
    }

    fun providePrefs(context: Context, prefsName: String): SharedPreferences =
            BinaryPreferencesBuilder(context)
                    .name(prefsName)
                    .allowBuildOnBackgroundThread()
                    .build()

    @SuppressWarnings("LongParameterList", "MaxLineLength")
    fun provideSecurePrefs(
            context: Context,
            prefsName: String,
            keySetName: String,
            prefFileName: String,
            masterKeyUri: String,
            dKeySetName: String,
            dPrefFileName: String,
            dMasterKeyUri: String
    ): SharedPreferences =
            BinaryPreferencesBuilder(context)
                    .name(prefsName)
                    .keyEncryption(TinkKeyEncryption(context, provideDAEAD(context, dKeySetName, dPrefFileName, dMasterKeyUri)))
                    .valueEncryption(TinkValueEncryption(context, provideAEAD(context, keySetName, prefFileName, masterKeyUri)))
                    .allowBuildOnBackgroundThread()
                    .build()

    private fun provideAEAD(context: Context, keySetName: String, prefFileName: String, masterKeyUri: String) =
            AndroidKeysetManager.Builder()
                    .withSharedPref(context, keySetName, prefFileName)
                    .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
                    .withMasterKeyUri(AndroidKeystoreKmsClient.PREFIX + masterKeyUri)
                    .build()
                    .keysetHandle
                    .getPrimitive(Aead::class.java)

    private fun provideDAEAD(context: Context, dKeySetName: String, dPrefFileName: String, dMasterKeyUri: String) =
            AndroidKeysetManager.Builder()
                    .withSharedPref(context, dKeySetName, dPrefFileName)
                    .withKeyTemplate(KeyTemplates.get("AES256_SIV"))
                    .withMasterKeyUri(AndroidKeystoreKmsClient.PREFIX + dMasterKeyUri)
                    .build()
                    .keysetHandle
                    .getPrimitive(DeterministicAead::class.java)
}