package com.example.ipz_project_2

import javax.crypto.Cipher
import java.io.IOException
import java.security.*
import java.util.*
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class AESEncoding {
    var secretKey: SecretKey

    companion object {

        fun encryptFile(fileData: ByteArray, secretKey: String): ByteArray {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, loadSecretKey(secretKey), IvParameterSpec(ByteArray(cipher.getBlockSize())))
            return cipher.doFinal(fileData)
        }

        fun decryptFile(fileData: ByteArray, secretKey: String): ByteArray {
            val decrypted: ByteArray
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, loadSecretKey(secretKey), IvParameterSpec(ByteArray(cipher.blockSize)))
            return cipher.doFinal(fileData)
        }

        @Throws(GeneralSecurityException::class, IOException::class)
        fun loadSecretKey(stored: String): Key {
            val data: ByteArray = Base64.getDecoder().decode(stored.toByteArray())
            return SecretKeySpec(data, 0, data.size, "AES")
        }

    }

    init {
        val secureRandom = SecureRandom()
        val keyGenerator = KeyGenerator.getInstance("AES")
        //generate a key with secure random
        keyGenerator?.init(128, secureRandom)
        secretKey = keyGenerator.generateKey()
    }
} 