package org.feup.group4.supermarket.service

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesService(context: Context, keyStore: String) {
    companion object{
        enum class KeyStore(val value: String){
            AUTH("auth"),
            CRYPTO("crypto")
        }
    }
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(keyStore, Context.MODE_PRIVATE)

    fun setValue(name: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(name, value)
        editor.apply()
    }
}