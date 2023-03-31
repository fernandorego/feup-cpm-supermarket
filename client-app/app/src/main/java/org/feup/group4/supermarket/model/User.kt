package org.feup.group4.supermarket.model

data class User(
    val email: String,
    val password: String,
    val name: String?,
    val accumulated_value: Double?,
    val user_img: String?,
    val is_admin: Boolean?,
)