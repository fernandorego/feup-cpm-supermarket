package org.feup.group4.supermarket.service

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.math.BigInteger
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.security.auth.x500.X500Principal

class CryptoService(context: Context) {
    private val sharedPreferencesService =
        SharedPreferencesService(context, SharedPreferencesService.Companion.KeyStore.CRYPTO.value)
    private val androidKeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }


    companion object {
        const val KEY_SIZE = 512
        const val KEY_NAME = "client_key"
        const val SERIAL_NR = 1234567890L
        const val ANDROID_KEYSTORE = "AndroidKeyStore"

        const val serverPublicKeyName = "server_public_key"
    }

    fun decryptMessage(message: ByteArray): String {
        val key = getServerPublicKey()

        // TODO: Might be needed to add padding transformation
        val cypher = Cipher.getInstance("RSA")
        cypher.init(Cipher.DECRYPT_MODE, key)
        return cypher.doFinal(message).toString()
    }

    fun signMessage(message: ByteArray): ByteArray {
        val key = getClientPrivateKey()

        val signer = Signature.getInstance("SHA256withRSA")
        signer.initSign(key)
        signer.update(message)
        return signer.sign()
    }

    fun verifyMessage(message: ByteArray, signature: ByteArray): Boolean {
        val key = getServerPublicKey()

        val signer = Signature.getInstance("SHA256withRSA")
        signer.initVerify(key)
        signer.update(message)
        return signer.verify(signature)
    }

    fun generateKeyPair(): KeyPair {
        val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_NAME,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        ).run {
            setDigests(KeyProperties.DIGEST_SHA512, KeyProperties.DIGEST_SHA256)
            setKeySize(KEY_SIZE)
            setCertificateSerialNumber(BigInteger.valueOf(SERIAL_NR))
            setCertificateSubject(X500Principal("CN=$KEY_NAME"))
            build()
        }
        KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_RSA,
            ANDROID_KEYSTORE
        ).run {
            initialize(parameterSpec)
            sharedPreferencesService.setValue("client_public_key", generateKeyPair().public.encoded.toString())
            return generateKeyPair()
        }
    }

    fun setServerPublicKey(key: String) {
        sharedPreferencesService.setValue(serverPublicKeyName, key)
        // TODO: Might be needed to convert from PEM to DER format
    }

    private fun getServerPublicKey(): PublicKey {
        val serverPublicKeyString =
            sharedPreferencesService.sharedPreferences.getString(serverPublicKeyName, null)
        val keySpec = X509EncodedKeySpec(serverPublicKeyString?.toByteArray(Charsets.UTF_8))
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec)
    }

    private fun getClientPrivateKey(): PrivateKey {
       return androidKeyStore.getKey(KEY_NAME, null) as PrivateKey
    }
}