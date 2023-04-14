package org.feup.group4.supermarket.model

import java.util.UUID

class Purchase(
    private val products: ArrayList<Pair<Product, Int>> = ArrayList(),
    var discount: Boolean? = null,
    var coupon: UUID? = null
) {
    fun addProduct(product: Product, quantity: Int = 1) : Pair<Int, Product> {
        for (i in 0 until products.size) {
            if (products[i].first == product) {
                products[i] = Pair(products[i].first, products[i].second + quantity)
                return Pair(i, products[i].first)
            }
        }
        products.add(Pair(product, quantity))
        return Pair(products.size - 1, product)
    }

    fun removeProduct(product: Product, quantity: Int? = null) {
        for (i in 0 until products.size) {
            if (products[i].first == product) {
                if (quantity == null) {
                    products.removeAt(i)
                    return
                } else {
                    products[i] = Pair(products[i].first, products[i].second - quantity)
                    if (products[i].second == 0) {
                        products.removeAt(i)
                    }
                    return
                }
            }
        }
    }

    fun getTotalPrice(): Double {
        var totalPrice = 0.0
        for (product in products) {
            totalPrice += product.first.price * product.second
        }
        return totalPrice
    }

    fun getProducts(): ArrayList<Pair<Product, Int>> {
        return products
    }
}