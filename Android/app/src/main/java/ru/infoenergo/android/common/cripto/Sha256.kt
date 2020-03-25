package ru.infoenergo.android.common.cripto

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class Sha256 private constructor() {
    companion object {
        @Volatile
        private var INSTANCE: Sha256? = null

        fun getInstance(): Sha256 {
            return INSTANCE ?: synchronized(this) {
                Sha256().also {
                    INSTANCE = it
                }
            }
        }
    }

    fun getHash(s: String): String? {
        val digest: MessageDigest
        val algorithm = "SHA-256"
        try {
            digest = MessageDigest.getInstance(algorithm)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return null
        }
        val encodedHash = digest.digest(s.toByteArray(StandardCharsets.UTF_8))
        return Base64.encodeToString(encodedHash, Base64.DEFAULT)
    }
}