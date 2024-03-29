package org.feup.group4.supermarket.fragments.client

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.model.Purchase
import org.feup.group4.supermarket.service.PurchaseService
import org.feup.group4.supermarket.service.QRService

class PurchaseQRDialogFragment : AppCompatDialogFragment() {
    class PurchaseQRDialog(
        activity: FragmentActivity,
        private val purchase: Purchase
    ) :
        AppCompatDialog(activity) {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setCancelable(false)
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_purchase_qr)

            val b64PurchaseJson = PurchaseService(context).getSignedPurchasePayload(purchase)

            findViewById<ImageView>(R.id.qr_image)?.setImageBitmap(
                QRService.generateQRCode(
                    b64PurchaseJson,
                    600,
                    600
                )
            )

            findViewById<Button>(R.id.close_button)?.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        return PurchaseQRDialog(
            requireActivity(), Gson().fromJson(
                arguments?.getString("purchase"),
                Purchase::class.java
            )
        )
    }

    companion object {
        fun newInstance(purchase: Purchase): PurchaseQRDialogFragment {
            val bundle = Bundle()
            bundle.putString("purchase", Gson().toJson(purchase))
            val fragment = PurchaseQRDialogFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}