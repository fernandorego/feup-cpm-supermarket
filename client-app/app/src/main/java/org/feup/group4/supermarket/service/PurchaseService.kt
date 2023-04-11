package org.feup.group4.supermarket.service

import android.content.Context
import android.util.Base64
import android.widget.Toast
import com.google.gson.Gson
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.activities.client.ClientActivity
import org.feup.group4.supermarket.model.Purchase

class PurchaseService(private val context: Context) {
    companion object {
        fun getPurchaseJson(purchase: Purchase): String {
            val cart: ArrayList<Map<String, String>> = ArrayList()
            purchase.getProducts().forEach() {
                cart.add(mapOf("uuid" to it.first.toString(), "quantity" to it.second.toString()))
            }

            return Gson().toJson(
                mapOf(
                    "user_uuid" to ClientActivity.user.uuid,
                    "cart" to cart,
                    "discount" to purchase.discount,
                    "coupon" to purchase.coupon,
                )
            )
        }
    }

    fun getSignedPurchasePayload(purchase: Purchase): String {
        val cryptoService = CryptoService(context)
        val purchaseJson = getPurchaseJson(purchase)
        val b64Message = Base64.encodeToString(purchaseJson.toByteArray(), Base64.DEFAULT)
        val signature = cryptoService.getMessageSignature(b64Message.toByteArray())
        val b64Signature = Base64.encodeToString(signature, Base64.NO_WRAP)

        return Gson().toJson(
            mapOf(
                "b64MessageString" to b64Message,
                "Signature" to b64Signature
            )
        )
    }

    fun forwardClientPurchase(messageWithSignature: String, callback: AfterRequest) {
        val messageJSON = Gson().fromJson(messageWithSignature, Map::class.java)
        if (messageJSON["b64MessageString"] == null || messageJSON["Signature"] == null) {
            callback(400, context.getString(R.string.purchase_invalid_message))
            return
        }

        HttpService(context, callback).post(
            "/purchase",
            Gson().toJson(mapOf("b64MessageString" to messageJSON["b64MessageString"] as String)),
            mapOf("Signature" to messageJSON["Signature"] as String)
        )
    }
}