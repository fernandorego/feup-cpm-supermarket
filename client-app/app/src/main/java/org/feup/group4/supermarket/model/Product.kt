package org.feup.group4.supermarket.model

import java.util.*

data class Product(
    val name: String,
    val price: Double,
    val uuid: UUID? = null,
)