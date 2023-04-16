package org.feup.group4.supermarket.service

import android.content.Context
import org.feup.group4.supermarket.model.User

class UserService(
    context: Context,
    afterRequest: AfterRequest?,
    afterConnectException: (() -> Unit)? = null
) : HttpService(context, afterRequest, afterConnectException) {
    fun getUserFromServer() = get("/user")

    fun getUserFromStorage() = run {
        val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        User(
            nickname = sharedPref.getString("nickname", null) ?: "",
            name = sharedPref.getString("name", null),
            accumulated_value = sharedPref.getFloat("accumulated_value", 0f).toDouble(),
            is_admin = sharedPref.getBoolean("is_admin", false),
            password = ""
        )
    }

    fun saveUser(user: User) {
        val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("nickname", user.nickname)
            putString("name", user.name)
            putFloat("accumulated_value", user.accumulated_value?.toFloat() ?: 0f)
            putBoolean("is_admin", user.is_admin ?: false)
            commit()
        }
    }
}
