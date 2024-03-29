package org.feup.group4.supermarket.fragments.client

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.gson.Gson
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.fragments.terminal.DismissCallback
import org.feup.group4.supermarket.model.Purchase
import org.feup.group4.supermarket.service.NFCReaderService.Companion.NFC_PREF_SEND
import org.feup.group4.supermarket.service.NFCSenderService
import org.feup.group4.supermarket.service.PurchaseService

class SendNFCPurchaseDialogFragment : AppCompatDialogFragment() {
    class SendNFCPurchaseDialog(
        context: Context,
        private val content: String,
        private val dismissCallback: DismissCallback
    ) :
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

            NFCSenderService.setByteArray(content.toByteArray())
            PreferenceManager.getDefaultSharedPreferences(ownerActivity).edit()
                .putBoolean(NFC_PREF_SEND, true).apply()

            setOnDismissListener {
                PreferenceManager.getDefaultSharedPreferences(ownerActivity).edit()
                    .putBoolean(NFC_PREF_SEND, false).apply()
            }

            setOnDismissListener {
                dismissCallback.invoke()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val b64PurchaseJson = PurchaseService(activity as Context).getSignedPurchasePayload(
            arguments?.getString("purchase")?.let { Gson().fromJson(it, Purchase::class.java) }!!,
        )
        return SendNFCPurchaseDialog(activity as Context, b64PurchaseJson) {
            dismiss()
        }
    }

    companion object {
        fun newInstance(purchase: Purchase): SendNFCPurchaseDialogFragment {
            val bundle = Bundle()
            bundle.putString("purchase", Gson().toJson(purchase))
            val fragment = SendNFCPurchaseDialogFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}