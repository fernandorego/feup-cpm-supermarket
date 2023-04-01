package org.feup.group4.supermarket.service

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import org.feup.group4.supermarket.model.Card
import org.feup.group4.supermarket.model.User
import java.security.KeyPair
import java.security.KeyPairGenerator

class AuthService(context: Context, afterRequest: AfterRequest?) :
    HttpService(context, afterRequest) {

    private fun generateKeyPair(): KeyPair {
        val kpg: KeyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_RSA,
            "AndroidKeyStore"
        )
        val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
            "private_key",
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        ).run {
            setDigests(KeyProperties.DIGEST_SHA512)
            build()
        }
        kpg.initialize(parameterSpec)
        return kpg.generateKeyPair()
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

    fun register(name: String, nickname: String, password: String, card_number: String, card_cvv: String, card_date: String) {
        val keypair = generateKeyPair()
        post(
            "/register",
            Gson().toJson(
                User(
                    nickname,
                    password,
                    name,
                    Card(card_number, card_cvv, card_date),
                    keypair.public.encoded.toString(),
                    null,
                    null,
                    null
                )
            )
        )
    }
}