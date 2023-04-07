package org.feup.group4.supermarket.service

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import org.feup.group4.supermarket.model.Card
import org.feup.group4.supermarket.model.User
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import javax.security.auth.x500.X500Principal

class AuthService(context: Context, afterRequest: AfterRequest?) :
    HttpService(context, afterRequest) {

    companion object {
        const val KEY_SIZE = 512
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val KEYNAME = "key_id"
        const val SERIALNR = 1234567890L
    }

    private fun generateClientKeyPair(): KeyPair {
        val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEYNAME,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        ).run {
            setDigests(KeyProperties.DIGEST_SHA512, KeyProperties.DIGEST_SHA256)
            setKeySize(KEY_SIZE)
            setCertificateSerialNumber(BigInteger.valueOf(SERIALNR))
            setCertificateSubject(X500Principal("CN=$KEYNAME"))
            build()
        }
        KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_RSA,
            ANDROID_KEYSTORE
        ).run {
            initialize(parameterSpec)
            return generateKeyPair()
        }
    }

    fun setToken(token: String?) = setValue(tokenStoreKey, token)
    fun setServerPublicKey(key: String) = setValue(serverPublicKey, key)

    private fun setValue(name: String, value: String?) {
        val sharedPreferences =
            context.getSharedPreferences(keyStore, AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(name, value)
        editor.apply()
    }

    fun login(nickname: String, password: String) =
        post(
            "/getToken",
            Gson().toJson(User(nickname, password, null, null, null, null, null, null))
        )

    fun register(
        name: String,
        nickname: String,
        password: String,
        card_number: String,
        card_cvv: String,
        card_date: String
    ) {
        val clientKeyPair = generateClientKeyPair()
        // TODO: save clientPrivKey
        //setValue(clientPrivateKeyStoreKey, clientKeyPair.private.encoded.toString())
        post(
            "/register", Gson().toJson(
                User(
                    nickname, password, name,
                    Card(card_number, card_cvv, card_date),
                    clientKeyPair.public.encoded.toString(),
                    null, null, null
                )
            )
        )
    }
}