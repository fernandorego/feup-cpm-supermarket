package org.feup.group4.supermarket.model

data class User(
    val email: String,
    val password: String,
    val name: String?,
    val is_admin: Boolean?,
    val user_img: String?,
)