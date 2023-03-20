package org.feup.group4.supermarket

import com.google.gson.Gson
import org.feup.group4.supermarket.activities.LoginActivity
import org.feup.group4.supermarket.activities.RegisterActivity
import org.feup.group4.supermarket.models.User
import org.feup.group4.supermarket.services.http.HttpServices

fun loginService(act: LoginActivity, baseAddress: String, port: String, email: String, password: String) {
    val json = Gson().toJson(User(email,password,null,null))
    HttpServices(act, baseAddress, port).post("/getToken", json)
}

fun registerService(act: RegisterActivity, baseAddress: String, port: String, name: String, email: String, password: String) {
    val json = Gson().toJson(User(email, password, name,null))
    HttpServices(act, baseAddress, port).post("/register", json)
}