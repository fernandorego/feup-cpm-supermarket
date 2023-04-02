package org.feup.group4.supermarket.service

import android.content.Context
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import org.feup.group4.supermarket.model.Card
import org.feup.group4.supermarket.model.User
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PublicKey
import javax.security.auth.x500.X500Principal

object CryptoConstants {
    const val KEY_SIZE = 512
    const val ANDROID_KEYSTORE = "AndroidKeyStore"
    const val KEYNAME = "key_id"
    const val SERIALNR = 1234567890L
}

class AuthService(context: Context, afterRequest: AfterRequest?) :
    HttpService(context, afterRequest) {

    private fun generateKeyPair(): PublicKey {
        val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
            CryptoConstants.KEYNAME,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        ).run {
            setDigests(KeyProperties.DIGEST_SHA512, KeyProperties.DIGEST_SHA256)
            setKeySize(CryptoConstants.KEY_SIZE)
            setCertificateSerialNumber(BigInteger.valueOf(CryptoConstants.SERIALNR))
            setCertificateSubject(X500Principal("CN=" + CryptoConstants.KEYNAME))
            build()
        }
        KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_RSA,
            CryptoConstants.ANDROID_KEYSTORE
        ).run {
            initialize(parameterSpec)
            return generateKeyPair().public
        }
    }

    fun setToken(token: String) {
        val sharedPreferences =
            context.getSharedPreferences(keyStore, AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(tokenStoreKey, token)
        editor.apply()
    }

    fun login(nickname: String, password: String) =
        post("/getToken", Gson().toJson(User(nickname, password, null, null, null, null, null, null)))

    fun register(name: String, nickname: String, password: String, card_number: String, card_cvv: String, card_date: String) =
        post("/register", Gson().toJson(User(nickname, password, name, Card(card_number, card_cvv, card_date), generateKeyPair().encoded.toString(), null, null, null)))
}