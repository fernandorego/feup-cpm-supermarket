package org.feup.group4.supermarket.service

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import org.feup.group4.supermarket.model.User

class AuthService(context: Context, afterRequest: AfterRequest?) :
    HttpService(context, afterRequest) {
    fun setToken(token: String) {
        val sharedPreferences =
            context.getSharedPreferences(tokenStore, AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(tokenStoreKey, token)
        editor.apply()
    }

    fun login(email: String, password: String) =
        post("/getToken", Gson().toJson(User(email, password, null, null, null)))

    fun register(name: String, email: String, password: String) =
        post("/register", Gson().toJson(User(email, password, name, null, null)))
}