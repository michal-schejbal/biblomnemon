package com.ginoskos.biblomnemon.core.security

interface ICrypto {
    fun encrypt(plaintext: String, aad: String? = null): String
    fun decrypt(ciphertext: String, aad: String? = null): String
}