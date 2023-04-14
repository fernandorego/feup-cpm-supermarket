package org.feup.group4.supermarket.model

import java.util.*
import kotlin.collections.ArrayList

data class Receipt (
    var created_at: String? = null,
    var total_price: Double = 0.0,
    val cart: ArrayList<Product>,
    var uuid: UUID? = null
)