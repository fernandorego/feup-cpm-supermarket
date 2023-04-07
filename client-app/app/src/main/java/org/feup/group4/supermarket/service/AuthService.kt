package org.feup.group4.supermarket.service

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import org.feup.group4.supermarket.model.Card
import org.feup.group4.supermarket.model.User
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.PublicKey
import javax.security.auth.x500.X500Principal

class AuthService(context: Context, afterRequest: AfterRequest?) :
    HttpService(context, afterRequest) {
    private val cryptoService = CryptoService(context)

    fun setToken(token: String) = sharedPreferencesService.setValue(tokenStoreKey, token)

    fun setServerPublicKey(key: String) = cryptoService.setServerPublicKey(key)

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
    ) =
        post(
            "/register",
            Gson().toJson(
                User(
                    nickname,
                    password,
                    name,
                    Card(card_number, card_cvv, card_date),
                    cryptoService.generateKeyPair().public.encoded.toString(),
                    null,
                    null,
                    null
                )
            )
        )
}