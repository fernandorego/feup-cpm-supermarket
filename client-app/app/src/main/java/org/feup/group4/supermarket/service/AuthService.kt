package org.feup.group4.supermarket.service

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
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
    private val cryptoService = CryptoService(context)

    fun setToken(token: String) = sharedPreferencesService.setValue(tokenStoreKey, token)
    fun setServerPrivateKey(key: String) = cryptoService.setServerPrivateKey(key)

    fun login(nickname: String, password: String) {
        val publicKey = CryptoService(context).generateKeyPair().public
        post(
            "/getToken",
            Gson().toJson(
                User(
                    nickname,
                    password,
                    Base64.encodeToString(publicKey.encoded, Base64.DEFAULT)
                )
            )
        )
    }

    fun register(
        name: String,
        nickname: String,
        password: String,
        card_number: String,
        card_cvv: String,
        card_date: String
    ) =
        post(
            "/register",
            Gson().toJson(
                User(
                    nickname,
                    password,
                    Base64.encodeToString(
                        cryptoService.generateKeyPair().public.encoded,
                        Base64.DEFAULT
                    ),
                    name,
                    Card(card_number, card_cvv, card_date)
                )
            )
        )
}