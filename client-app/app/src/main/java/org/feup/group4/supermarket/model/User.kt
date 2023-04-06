package org.feup.group4.supermarket.model

data class User(
    val nickname: String,
    val password: String,
    val name: String?,
    val card: Card?,
    val public_key: String?,
    val accumulated_value: Double?,
    val user_img: String?,
    val is_admin: Boolean?,
)