package org.feup.group4.supermarket.model

import java.util.*

data class Product(
    val name: String,
    val euros: Int,
    val cents: Int,
){
    lateinit var uuid: UUID

    constructor(uuid: UUID, name: String, euros: Int, cents: Int) : this(name, euros, cents) {
        this.uuid = uuid
    }
}

data class ProductTest(
    val name: String,
    val price: Double,
    val uuid: UUID? = null,
)