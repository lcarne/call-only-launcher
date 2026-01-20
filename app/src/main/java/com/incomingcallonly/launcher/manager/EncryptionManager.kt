package com.incomingcallonly.launcher.manager

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptionManager @Inject constructor() {

    private val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
        load(null)
    }

    init {
        createKeyIfNotExists()
    }

    private fun createKeyIfNotExists() {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEY_STORE
            )
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
    }

    private fun getSecretKey(): SecretKey {
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }

    fun encrypt(data: String): String {
        return try {
            if (data.isBlank()) return data
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
            
            // Format: IV|EncryptedData (Base64 encoded)
            val combined = ByteArray(iv.size + encryptedBytes.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)
            
            Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            data // Fallback to original if encryption fails
        }
    }

    fun decrypt(encryptedData: String): String {
        return try {
            if (encryptedData.isBlank()) return encryptedData
            
            // Attempt to decode Base64
            val combined = try {
                Base64.decode(encryptedData, Base64.NO_WRAP)
            } catch (e: IllegalArgumentException) {
                // Not a valid Base64 string, likely plain text legacy data
                return encryptedData
            }

            // GCM IV length is 12 bytes
            val ivLength = 12
            if (combined.size < ivLength) {
                // Too short to be valid encrypted data
                return encryptedData
            }

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(128, combined, 0, ivLength)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)

            val decoded = cipher.doFinal(combined, ivLength, combined.size - ivLength)
            String(decoded, Charsets.UTF_8)
        } catch (e: Exception) {
            // Decryption failed (wrong key, bad data, or plain text that accidentally looked like Base64)
            // Return original data as fallback for migration
            encryptedData
        }
    }

    companion object {
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "IncomingCallOnlyKey"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
    }
}
