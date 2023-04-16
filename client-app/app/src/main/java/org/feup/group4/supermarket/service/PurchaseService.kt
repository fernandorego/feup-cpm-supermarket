package org.feup.group4.supermarket.service

import android.content.Context
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.activities.client.ClientActivity
import org.feup.group4.supermarket.model.Purchase
import org.feup.group4.supermarket.model.Receipt

class PurchaseService(private val context: Context) {
    companion object {
        fun getPurchaseJson(purchase: Purchase): String {
            val cart: ArrayList<Map<String, Any>> = ArrayList()
            purchase.getProducts().forEach() {
                cart.add(mapOf("uuid" to it.first.uuid.toString(), "quantity" to it.second))
            }

            return Gson().toJson(
                mapOf(
                    "user_uuid" to ClientActivity.user.uuid,
                    "discount" to (purchase.discount ?: false),
                    "coupon" to purchase.coupon,
                    "cart" to cart,
                )
            )
        }
    }

    fun getSignedPurchasePayload(purchase: Purchase): String {
        val cryptoService = CryptoService(context)
        val purchaseJson = getPurchaseJson(purchase)
        val b64Message = Base64.encodeToString(
            purchaseJson.toByteArray(charset = Charsets.UTF_8),
            Base64.DEFAULT
        )
        val signature =
            cryptoService.getMessageSignature(b64Message.toByteArray(charset = Charsets.UTF_8))
        val b64Signature = Base64.encodeToString(signature, Base64.NO_WRAP)

        return Gson().toJson(
            mapOf(
                "b64MessageString" to b64Message,
                "Signature" to b64Signature
            )
        )
    }

    fun forwardClientPurchase(messageWithSignature: String, callback: AfterRequest) {
        try {
            val messageJSON = Gson().fromJson(messageWithSignature, Map::class.java)

            if (messageJSON["b64MessageString"] == null || messageJSON["Signature"] == null) {
                callback(400, context.getString(R.string.purchase_invalid_message))
                return
            }

            HttpService(context, callback).post(
                "/purchase",
                Gson().toJson(mapOf("b64MessageString" to (messageJSON["b64MessageString"] as String))),
                mapOf("Signature" to messageJSON["Signature"] as String)
            )
        } catch (e: Exception) {
            callback(400, context.getString(R.string.purchase_invalid_message))
            return
        }
    }

    fun getReceipts(callback: (List<Receipt>) -> Unit) {
        fun afterRequest(statusCode: Int, body: String?) {
            if (statusCode != 200) {
                if (statusCode == 204) {
                    Log.w("PurchaseService", "No receipts found")
                } else {
                    Log.w("PurchaseService", "Error getting receipts: $statusCode: $body")
                }
                callback(emptyList())
                return
            }

            val receipts = Gson().fromJson(body, Array<Receipt>::class.java).toList()
            callback(receipts)
        }

        val userUUID = ClientActivity.user.uuid
        val message = Gson().toJson(userUUID)
        val b64Message =
            Base64.encodeToString(message.toByteArray(charset = Charsets.UTF_8), Base64.NO_WRAP)
        val signature =
            CryptoService(context).getMessageSignature(b64Message.toByteArray(charset = Charsets.UTF_8))
        val base64Signature = Base64.encodeToString(signature, Base64.NO_WRAP)
        HttpService(context, ::afterRequest)
            .get(
                "/purchases/$userUUID",
                mapOf("Signature" to base64Signature, "Signed-Message" to b64Message)
            )
    }
}