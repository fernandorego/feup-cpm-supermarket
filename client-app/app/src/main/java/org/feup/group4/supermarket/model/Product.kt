package org.feup.group4.supermarket.model

import java.util.*

data class Product(
    val name: String,
    val price: Double,
    val uuid: UUID? = null,
    var image: String = "",
    val quantity: Int = 1
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Product

        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return uuid?.hashCode() ?: 0
    }
}
