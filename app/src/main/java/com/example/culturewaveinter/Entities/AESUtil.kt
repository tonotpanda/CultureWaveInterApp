package com.example.culturewaveinter.Entities

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AESUtil {
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val SECRET_KEY = "1234567890123456" // Clave de 16 caracteres (128 bits)
    private const val INIT_VECTOR = "abcdefghijklmnop" // IV de 16 caracteres

    // Encriptar
    fun encrypt(value: String): String {
        val iv = IvParameterSpec(INIT_VECTOR.toByteArray(Charsets.UTF_8))
        val skeySpec = SecretKeySpec(SECRET_KEY.toByteArray(Charsets.UTF_8), "AES")

        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)

        val encrypted = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encrypted, Base64.NO_WRAP)
    }

    // Desencriptar
    fun decrypt(encrypted: String): String {
        val iv = IvParameterSpec(INIT_VECTOR.toByteArray(Charsets.UTF_8))
        val skeySpec = SecretKeySpec(SECRET_KEY.toByteArray(Charsets.UTF_8), "AES")

        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)

        val original = cipher.doFinal(Base64.decode(encrypted, Base64.NO_WRAP))
        return String(original, Charsets.UTF_8)
    }
}
