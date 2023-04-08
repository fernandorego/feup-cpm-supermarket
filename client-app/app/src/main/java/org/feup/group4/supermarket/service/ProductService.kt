package org.feup.group4.supermarket.service

import android.content.Context
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import org.feup.group4.supermarket.model.Product
import java.util.UUID
import java.util.logging.Logger

class ProductService(val context: Context) {
    fun getEncryptedProduct(uuid: UUID, callback: (ByteArray) -> Unit) {
        var encryptedProduct: ByteArray
        fun afterRequest(statusCode: Int, body: String?) {
            if (statusCode != 200) {
                Log.w("ProductService", "Error getting product: $statusCode: $body")
                return
            }

            // Decode Base64 string
            encryptedProduct = Base64.decode(body, Base64.DEFAULT)

            callback(encryptedProduct)
        }
        HttpService(context, ::afterRequest).get("/product/$uuid")
    }


    fun getDecryptedProduct(uuid: UUID, callback: (Product) -> Unit) {
        fun afterRequest(encryptedProduct: ByteArray) {
            val cryptoService = CryptoService(context)
            val jsonProduct =
                Base64.decode(cryptoService.decryptMessage(encryptedProduct), Base64.DEFAULT)
            val product =
                Gson().fromJson(jsonProduct.toString(Charsets.UTF_8), Product::class.java)

            callback(product)
        }
        getEncryptedProduct(uuid, ::afterRequest)
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
        val cryptoService = CryptoService(context)
        val jsonProduct = Gson().toJson(unsignedProduct)
        val base64JsonProduct = Base64.encode(jsonProduct.toByteArray(), Base64.DEFAULT)
        val signedProduct = cryptoService.signMessage(base64JsonProduct)
        val base64SignedProduct = Base64.encode(signedProduct, Base64.DEFAULT)

        fun afterRequest(statusCode: Int, body: String?) {
            if (statusCode != 200) {
                Log.w("ProductService", "Error creating product: $statusCode: $body")
                return
            }

            val product = Gson().fromJson(body, Product::class.java)
            callback(product)
        }
        HttpService(context, ::afterRequest).post("/product", base64SignedProduct.toString())
    }
}