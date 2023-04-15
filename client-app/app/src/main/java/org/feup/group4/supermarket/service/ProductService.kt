package org.feup.group4.supermarket.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import org.feup.group4.supermarket.model.Product
import java.util.*


class ProductService(val context: Context) {
    fun getEncryptedProduct(uuid: UUID, callback: (String) -> Unit) {
        var encryptedProduct: String
        fun afterRequest(statusCode: Int, body: String?) {
            if (statusCode != 200) {
                Log.w("ProductService", "AAAAError getting product: $statusCode: $body")
                return
            }

            // Decode Base64 string
            encryptedProduct =
                (Base64.decode(body, Base64.DEFAULT)).toString(charset = Charsets.UTF_8)
            callback(encryptedProduct)
            return
        }
        HttpService(context, ::afterRequest).get("/product/$uuid")
    }

    fun getDecryptedProduct(uuid: UUID, callback: (Product) -> Unit) {
        fun afterRequest(encryptedProduct: String) {
            val product = decryptProduct(encryptedProduct)

            callback(product)
        }
        getEncryptedProduct(uuid, ::afterRequest)
    }

    fun decryptProduct(encryptedProduct: String): Product {
        val cryptoService = CryptoService(context)
        val encryptedProductMap =
            Gson().fromJson(encryptedProduct, Map::class.java)

        val decryptedUUID =
            cryptoService.decryptMessage(
                Base64.decode(
                    (encryptedProductMap["uuid"] as String).toByteArray(),
                    Base64.DEFAULT
                )
            )
        val name =
            cryptoService.decryptMessage(
                Base64.decode(
                    (encryptedProductMap["name"] as String).toByteArray(),
                    Base64.DEFAULT
                )
            )
        val price =
            cryptoService.decryptMessage(
                Base64.decode(
                    (encryptedProductMap["price"] as String).toByteArray(),
                    Base64.DEFAULT
                )
            )

        return Product(
            String(name),
            String(price).toDouble(),
            UUID.fromString(String(decryptedUUID))
        )
    }

    fun getProducts(callback: (List<Product>) -> Unit) {
        fun afterRequest(statusCode: Int, body: String?) {
            if (statusCode != 200) {
                if (statusCode == 204) {
                    Log.w("ProductService", "No products found")
                } else {
                    Log.w("ProductService", "Error getting products: $statusCode: $body")
                }
                callback(emptyList())
                return
            }


            Log.w("ProductService", "Products: $body")
            val products = Gson().fromJson(body, Array<Product>::class.java).toList()
            callback(products)
        }
        HttpService(context, ::afterRequest).get("/product")
    }

    fun createReplaceProduct(unsignedProduct: Product, callback: (Product) -> Unit) {
        fun afterRequest(statusCode: Int, body: String?) {
            if (statusCode != 200) {
                Log.w("ProductService", "Error creating product: $statusCode: $body")
                return
            }

            val product = Gson().fromJson(body, Product::class.java)
            callback(product)
        }

        val cryptoService = CryptoService(context)
        val jsonProduct = Gson().toJson(unsignedProduct)
        val b64Message =
            Base64.encodeToString(jsonProduct.toByteArray(charset = Charsets.UTF_8), Base64.DEFAULT)
        val signature =
            cryptoService.getMessageSignature(b64Message.toByteArray(charset = Charsets.UTF_8))
        val b64Signature = Base64.encodeToString(signature, Base64.NO_WRAP)

        HttpService(context, ::afterRequest).post(
            "/product",
            Gson().toJson(
                mapOf(
                    "b64MessageString" to b64Message
                )
            ),
            mapOf("Signature" to b64Signature)
        )

    }

    fun deleteProduct(product: Product, callback: () -> Unit) {
        fun afterRequest(statusCode: Int, body: String?) {
            if (statusCode != 204) {
                Log.w("ProductService", "Error deleting product: $statusCode: $body")
                return
            }
            callback()
        }

        val cryptoService = CryptoService(context)
        val message = Gson().toJson(product.uuid)
        val b64Message =
            Base64.encodeToString(message.toByteArray(charset = Charsets.UTF_8), Base64.NO_WRAP)
        val signature =
            cryptoService.getMessageSignature(b64Message.toByteArray(charset = Charsets.UTF_8))
        val base64Signature = Base64.encodeToString(signature, Base64.NO_WRAP)

        HttpService(context, ::afterRequest).delete(
            "/product/${product.uuid}",
            mapOf("Signature" to base64Signature, "Signed-Message" to b64Message)
        )
    }

    fun getProductImage(product: UUID, callback: (String) -> Unit) {
        fun afterRequest(statusCode: Int, body: String?) {
            if (statusCode != 200) {
                Log.w("ProductService", "Error getting product image: $statusCode: $body")
                return
            }
            val imageJson =
                Gson().fromJson(body!!, Map::class.java)
            callback(imageJson["b64Image"] as String)
        }

        HttpService(context, ::afterRequest).get("/product/${product}/image")
    }
}