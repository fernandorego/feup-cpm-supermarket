package org.feup.group4.supermarket.service

import android.content.Context
import android.util.Base64
import android.util.Log
import java.util.UUID
import java.util.logging.Logger

class ProductService(val context: Context) {
    fun getEncryptedProduct(uuid: UUID, callback: (ByteArray) -> Unit) {
        var encryptedProduct: ByteArray
        fun afterRequest(statusCode: Int, body: String?) {
            if (statusCode != 200) {
                Log.w("ProductService","Error getting product: $body")
            }

            // Decode Base64 string
            encryptedProduct = Base64.decode(body, Base64.DEFAULT)

            callback(encryptedProduct)
        }
        HttpService(context, ::afterRequest).get("/product/$uuid")
    }


    fun getDecryptedProduct(uuid: UUID, callback: (ByteArray) -> Unit) {
        fun afterRequest(encryptedProduct: ByteArray) {
            val cryptoService = CryptoService(context)
            val decryptedProduct =
                Base64.decode(cryptoService.decryptMessage(encryptedProduct), Base64.DEFAULT)

            callback(decryptedProduct)
        }
        getEncryptedProduct(uuid, ::afterRequest)
    }

    fun createUpdateProduct(unsignedProduct: ByteArray, callback: () -> Unit) {
        val cryptoService = CryptoService(context)
        val signedProduct = cryptoService.signMessage(unsignedProduct)
        fun afterRequest(statusCode: Int, body: String?) {
            if (statusCode != 200) {
                Log.w("ProductService", "Error creating product: $body")
            }

        }
        HttpService(context, ::afterRequest).post("/product", signedProduct.toString())
    }
}