package org.feup.group4.supermarket.models

data class User(
    val email: String,
    val password: String,
    val name: String?,
    val user_img: String?,
)