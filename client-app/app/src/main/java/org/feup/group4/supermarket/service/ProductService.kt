package org.feup.group4.supermarket.service

import android.content.Context
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import org.feup.group4.supermarket.model.Product
import org.feup.group4.supermarket.model.ProductTest
import java.util.*


class ProductService(val context: Context) {
    fun getEncryptedProduct(uuid: UUID, callback: (ByteArray) -> Unit) {
        var encryptedProduct: ByteArray
        fun afterRequest(statusCode: Int, body: String?) {
            if (statusCode != 200) {
                Log.w("ProductService", "AAAAError getting product: $statusCode: $body")
                return
            }

            // Decode Base64 string
            encryptedProduct = Base64.decode(body, Base64.DEFAULT)
            Log.w("ProductService", encryptedProduct.toString(charset = Charsets.UTF_8))

            callback(encryptedProduct)
            return
        }
        HttpService(context, ::afterRequest).get("/product/$uuid")
    }


    fun getDecryptedProduct(uuid: UUID, callback: (ProductTest) -> Unit) {
        fun afterRequest(encryptedProduct: ByteArray) {
            val product = decryptProduct(encryptedProduct)

            callback(product)
        }
        getEncryptedProduct(uuid, ::afterRequest)
    }

    fun decryptProduct(encryptedProduct: ByteArray): ProductTest {
        val cryptoService = CryptoService(context)

        var encryptedProductMap =
            Gson().fromJson(encryptedProduct.toString(charset = Charsets.UTF_8), Map::class.java)

        //Log.w("ProductService", Base64.decode((encryptedProductMap["uuid"] as String).toByteArray(), Base64.DEFAULT).toString(charset = Charsets.UTF_8))
        val decryptedUUID =
            cryptoService.decryptMessage((encryptedProductMap["uuid"] as String).toByteArray())
        val name =
            cryptoService.decryptMessage((encryptedProductMap["name"] as String).toByteArray())
        val price =
            cryptoService.decryptMessage((encryptedProductMap["price"] as String).toByteArray())

        return ProductTest(
            name.toString(),
            price.toString().toDouble(),
            UUID.fromString(decryptedUUID.toString())
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

    fun createUpdateProduct(unsignedProduct: Product, callback: (Product) -> Unit) {
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
        val signature =
            cryptoService.getMessageSignature(jsonProduct.toByteArray(charset = Charsets.UTF_8))
        val base64Signature = Base64.encodeToString(signature, Base64.DEFAULT)

        HttpService(context, ::afterRequest).post(
            "/product",
            Gson().toJson(
                mapOf(
                    "b64SignatureString" to base64Signature,
                    "message" to jsonProduct
                )
            )
        )

    }
}