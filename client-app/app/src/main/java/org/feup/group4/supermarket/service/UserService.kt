package org.feup.group4.supermarket.service

import android.content.Context

class UserService(context: Context, afterRequest: AfterRequest?) : HttpService(context, afterRequest) {
    fun getUser() = get("/getUser")
}
