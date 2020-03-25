package ru.infoenergo.android.common

import ru.infoenergo.android.common.cripto.RSAEncryptUtil
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey

class Crypto private constructor() {

    companion object {
        @Volatile
        private var INSTANCE: Crypto? = null

        fun getInstance(): Crypto {
            return INSTANCE ?: synchronized(this) {
                Crypto().also {
                    INSTANCE = it
                }
            }
        }
    }

    private var keys: KeyPair? = null
    var ticket_for_ticket: String? = null
    var ticket_for_token: String? = null
    var token: String? = null
    var token_use_key = 0

    fun getTokenToSend(): String {
        if (token==null)
            return ""
        var result = ""
        token_use_key++
        result += token + token_use_key
        if (getServKey()==null)
            return ""
        result = encrypt(result, getServKey()!!)
        return result
    }

    fun createKeys(key_file_dir: String) {
        keys = RSAEncryptUtil.generateKey()
    }


    fun getPubKey(): PublicKey? {
        if (keys != null)
            return keys!!.public
        return null
    }

    fun getPubKeyStr(): String? {
        if (keys != null)
            return RSAEncryptUtil.getKeyAsString(keys!!.public)
        return null
    }

    private var servKey: PublicKey? = null
    fun setServKey(pubKey: PublicKey) {
        servKey = pubKey
    }

    fun getServKey(): PublicKey? {
        return servKey
    }

    fun getPrivKey(): PrivateKey? {
        if (keys != null)
            return keys!!.private
        return null
    }

    fun getPublicKeyFromString(keyStr: String): PublicKey {
        return RSAEncryptUtil.getPublicKeyFromString(keyStr)
    }

    fun decrypt(criptStrB64: String, privateKey: PrivateKey): String {
        return RSAEncryptUtil.decrypt(criptStrB64, getPrivKey())

    }

    fun encrypt(text: String, pubKey: PublicKey): String {
        return RSAEncryptUtil.encrypt(text, pubKey)
    }
}