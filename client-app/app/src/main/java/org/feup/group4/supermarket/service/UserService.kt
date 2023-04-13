package org.feup.group4.supermarket.service

import android.content.Context
import org.feup.group4.supermarket.model.User

class UserService(context: Context, afterRequest: AfterRequest?) : HttpService(context, afterRequest) {
    fun getUser() = get("/user")

    fun saveUser(user: User) {
        val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("nickname", user.nickname)
            putString("name", user.name)
            putFloat("accumulated_value", user.accumulated_value?.toFloat() ?: 0f)
            commit()
        }
    }
}
