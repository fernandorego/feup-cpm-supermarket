package org.feup.group4.supermarket.fragments.client

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.model.Purchase
import org.feup.group4.supermarket.service.NFCReaderService.Companion.SEND_NFC_PREF
import org.feup.group4.supermarket.service.NFCSenderService
import org.feup.group4.supermarket.service.PurchaseService

class SendNFCPurchaseDialogFragment(private val purchase: Purchase) : AppCompatDialogFragment() {
    class SendNFCPurchaseDialog(context: Context, private val content: String) :
        AppCompatDialog(context) {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setCancelable(false)
            setTitle(context.resources.getString(R.string.scanning_nfc_title))
            setContentView(R.layout.dialog_nfc_progress)

            val progressText = findViewById<TextView>(R.id.nfc_progress_text)
            progressText?.text = context.resources.getString(R.string.scanning_nfc_message_sender)

            val cancelButton = findViewById<TextView>(R.id.cancel_button)
            cancelButton?.setOnClickListener {
                dismiss()
            }

            PreferenceManager.getDefaultSharedPreferences(ownerActivity).edit()
                .putBoolean(SEND_NFC_PREF, true).apply()

            setOnDismissListener {
                PreferenceManager.getDefaultSharedPreferences(ownerActivity).edit()
                    .putBoolean(SEND_NFC_PREF, false).apply()
            }

            val intent = Intent(context, NFCSenderService::class.java)
            intent.putExtra("content", content)
            context.startService(intent)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val b64PurchaseJson = PurchaseService(activity as Context).getSignedPurchasePayload(purchase)
        return SendNFCPurchaseDialog(activity as Context, b64PurchaseJson)
    }
}