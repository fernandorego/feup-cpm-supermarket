package org.feup.group4.supermarket

import org.feup.group4.supermarket.services.http.HttpInterface
import org.feup.group4.supermarket.services.http.HttpServices

fun getUser(act: HttpInterface, baseAddress: String, port: Int, token: String) {
    HttpServices(act, baseAddress, port).get("/user/getUser",token)
}