package com.ginoskos.biblomnemon.core.security

import android.content.Context
import com.example.nbaplayers.app.logger.ILogger
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import java.nio.charset.Charset
import java.util.Base64

enum class TinkKeyTemplate(val template: String) {
    /**
     * Default, general-purpose AEAD on Android.
     * Best for tokens, preferences, small/medium JSON/blobs stored locally or synced.
     */
    AES256_GCM("AES256_GCM"),

    /**
     * Misuse-resistant AEAD variant of GCM that better tolerates nonce reuse mistakes.
     * Slightly slower; good defensive default if you want extra safety.
     */
    AES256_GCM_SIV("AES256_GCM_SIV"),

    /**
     * AEAD alternative that performs well without AES hardware acceleration.
     * Useful on low-end/older CPUs and to ensure more uniform performance.
     */
    CHACHA20_POLY1305("CHACHA20_POLY1305"),

    /**
     * Deterministic AEAD (encrypts same plaintext+AAD to the same ciphertext).
     * Only for special cases like tokenization/equality checks. Leaks equality—avoid for general storage.
     */
    AES256_SIV_DETERMINISTIC("AES256_SIV"),

    /**
     * Streaming AEAD for large files; small 4KB segments reduce memory usage, increase I/O overhead.
     * Ideal for encrypting logs, backups, or media incrementally.
     */
    STREAMING_AES128_GCM_HKDF_4KB("AES128_GCM_HKDF_4KB"),

    /**
     * Streaming AEAD for large files; 1MB segments reduce per-chunk overhead.
     * Better when memory is available and you want higher throughput.
     */
    STREAMING_AES128_GCM_HKDF_1MB("AES128_GCM_HKDF_1MB");
}

/**
 * Wrapper around Tink AEAD (Authenticated Encryption with Associated Data).
 *
 * - Provides high-level String-based `encrypt` and `decrypt` methods.
 * - Uses Base64 to ensure ciphertext is safe for storage/transmission.
 * - Configurable key template (AES256_GCM, AES256_GCM_SIV, etc.).
 * - Automatically manages keyset storage via SharedPreferences,
 *   wrapped by a master key in Android Keystore.
 *
 * Default AAD (Associated Data) is the app package name, ensuring that
 * ciphertexts are bound to this app unless overridden per call.
 */
class TinkCrypto(
    private val context: Context,
    private val keyTemplate: TinkKeyTemplate = TinkKeyTemplate.AES256_GCM,
    private val charset: Charset = Charsets.UTF_8,
    private val defAad: String = context.packageName,
    private val logger: ILogger
) : ICrypto {

    private val KEYSET_NAME = "keyset"
    private val KEYSET_PREF_NAME = "tink_prefs"

    /**
     * Lazily initializes an AEAD instance:
     * - Registers AEAD configuration.
     * - Creates or loads a keyset in SharedPreferences, wrapped by a Keystore master key.
     * - Returns an AEAD primitive for encryption and decryption.
     */
    private val aead: Aead by lazy {
        AeadConfig.register()
        val appName = context.packageName.substringAfterLast('.')

        val keysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, KEYSET_PREF_NAME)
            .withKeyTemplate(KeyTemplates.get(keyTemplate.template))
            .withMasterKeyUri("android-keystore://${appName}_master_key")
            .build()
            .keysetHandle

        logger.i("Keyset initialized in SharedPreferences file=$KEYSET_PREF_NAME, key=$KEYSET_NAME")

        keysetHandle.getPrimitive(
            RegistryConfiguration.get(),
            Aead::class.java
        )
    }

    /**
     * Encrypts plaintext into a Base64-encoded ciphertext.
     *
     * @param plaintext The input text to encrypt.
     * @param aad Optional associated authenticated data. If not provided,
     *            defaults to the app package name.
     * @return Ciphertext as a Base64 string.
     */
    override fun encrypt(plaintext: String, aad: String?): String {
        logger.d("Encrypting data (len=${plaintext.length}) with AAD=${aad ?: defAad}")

        val plaintextBytes = plaintext.toByteArray(charset)
        val aadBytes = (aad ?: defAad).toByteArray(charset)
        val ciphertext = aead.encrypt(plaintextBytes, aadBytes)

        val encoded = Base64.getEncoder().encodeToString(ciphertext)
        logger.d("Encryption complete, ciphertext length=${encoded.length}")
        return encoded
    }

    /**
     * Decrypts a Base64-encoded ciphertext back into plaintext.
     *
     * @param ciphertext The Base64 string to decrypt.
     * @param aad Optional associated authenticated data. If not provided,
     *            defaults to the app package name.
     * @return Decrypted plaintext as a string.
     * @throws GeneralSecurityException if the ciphertext or AAD is invalid.
     */
    override fun decrypt(ciphertext: String, aad: String?): String {
        logger.d("Decrypting data (len=${ciphertext.length}) with AAD=${aad ?: defAad}")

        val ciphertextBytes = Base64.getDecoder().decode(ciphertext)
        val aadBytes = (aad ?: defAad).toByteArray(charset)
        val plaintext = aead.decrypt(ciphertextBytes, aadBytes)

        val decoded = plaintext.toString(charset)
        logger.d("Decryption complete, plaintext length=${decoded.length}")
        return decoded
    }
}