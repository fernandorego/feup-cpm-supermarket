package org.feup.group4.supermarket.model

import java.util.UUID

data class User(
    val nickname: String,
    val password: String,
    val public_key: String? = null,
    val name: String? = null,
    val card: Card? = null,
    val accumulated_value: Double? = null,
    val user_img: String? = null,
    val is_admin: Boolean? = null,
    val uuid: UUID? = null,
    val active_coupons: List<Coupon>? = null
)