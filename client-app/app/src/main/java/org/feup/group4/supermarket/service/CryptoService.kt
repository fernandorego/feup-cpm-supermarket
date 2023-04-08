package org.feup.group4.supermarket.service

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.math.BigInteger
import java.security.*
import java.security.spec.EncodedKeySpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAPublicKeySpec
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

        const val serverPrivateKeyName = "server_private_key"
    }

    fun decryptMessage(message: ByteArray): ByteArray {
        val key = getServerPrivateKey()

        // TODO: Might be needed to add padding transformation
        val cypher = Cipher.getInstance("RSA/NONE/PKCS1Padding")
        cypher.init(Cipher.DECRYPT_MODE, key)
        return cypher.doFinal(message)
    }

    fun getMessageSignature(message: ByteArray): ByteArray {
        val key = getClientPrivateKey()

        val signer = Signature.getInstance("SHA256withRSA")
        signer.initSign(key)
        signer.update(message)
        return signer.sign()
    }

    /*fun verifyMessage(message: ByteArray, signature: ByteArray): Boolean {
        val key = getServerPrivateKey()

        val signer = Signature.getInstance("SHA256withRSA")
        signer.initVerify(key)
        signer.update(message)
        return signer.verify(signature)
    }*/

    fun setServerPrivateKey(key: String) {
        sharedPreferencesService.setValue(serverPrivateKeyName, key)

        // TODO: Might be needed to convert from PEM to DER format
    }

    private fun getServerPrivateKey(): PrivateKey {
        val serverPrivateKeyString =
            sharedPreferencesService.sharedPreferences.getString(serverPrivateKeyName, null)

        val privateKey = serverPrivateKeyString!!.replace("\n", "")
            .replace("-----BEGIN RSA PRIVATE KEY-----", "")
            .replace("-----END RSA PRIVATE KEY-----", "").replace(Regex("\\s"), "");

        val keySpec = PKCS8EncodedKeySpec(Base64.decode(privateKey, Base64.DEFAULT))
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(keySpec)
    }

    /*
        fun getClientPublicKey(): PublicKey? {
            val entry: KeyStore.Entry = androidKeyStore.getEntry(KEY_NAME, null)
            if (entry !is KeyStore.PrivateKeyEntry) {
                Log.w("CryptoService", "Not an instance of a PrivateKeyEntry")
                return null
            }

            return entry.certificate.publicKey
        }
    */
    private fun getClientPrivateKey(): PrivateKey? {
        val entry: KeyStore.Entry = androidKeyStore.getEntry(KEY_NAME, null)
        if (entry !is KeyStore.PrivateKeyEntry) {
            Log.w("CryptoService", "Not an instance of a PrivateKeyEntry")
            return null
        }

        return entry.privateKey
    }

    fun generateKeyPair(): KeyPair {
        val keyPairGenerator =
            KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEYSTORE)

        val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_NAME,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        ).run {
            setDigests(KeyProperties.DIGEST_SHA512, KeyProperties.DIGEST_SHA256)
            setKeySize(KEY_SIZE)
            setCertificateSerialNumber(BigInteger.valueOf(SERIAL_NR))
            setCertificateSubject(X500Principal("CN=$KEY_NAME"))
            setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
            build()
        }

        keyPairGenerator.run {
            initialize(parameterSpec)
            return generateKeyPair()
        }
    }
}