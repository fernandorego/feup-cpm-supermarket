package org.feup.group4.supermarket.model

class Purchase {
    val products: LinkedHashMap<Product, Int> = LinkedHashMap()

    fun addProduct(product: Product, quantity: Int) {
        if (products.containsKey(product)) {
            products[product] = products[product]!! + quantity
        } else {
            products[product] = quantity
        }
    }

    fun removeProduct(product: Product, quantity: Int) {
        if (products.containsKey(product)) {
            products[product] = products[product]!! - quantity
            if (products[product]!! <= 0) {
                products.remove(product)
            }
        }
    }

    fun getTotalPrice(): Double {
        var totalPrice = 0.0
        for (product in products.keys) {
            totalPrice += product.euros * products[product]!!
            totalPrice += product.cents * products[product]!! / 100.0
        }
        return totalPrice
    }
}