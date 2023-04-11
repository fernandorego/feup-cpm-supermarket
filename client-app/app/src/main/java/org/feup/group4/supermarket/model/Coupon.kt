package org.feup.group4.supermarket.model

import java.time.LocalDate
import java.util.UUID

data class Coupon (
    val uuid: UUID,
    var expiration: LocalDate
)