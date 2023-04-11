package org.feup.group4.supermarket.fragments.client

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.activities.client.ClientActivity
import org.feup.group4.supermarket.activities.client.PurchaseActivity
import org.feup.group4.supermarket.adapters.coupons
import org.feup.group4.supermarket.model.Purchase

class PurchaseOptionsDialogFragment(private val purchase: Purchase) :
    AppCompatDialogFragment() {
    class PurchaseOptionsDialog(
        private val activity: FragmentActivity,
        private val purchase: Purchase
    ) :
        AppCompatDialog(activity) {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_purchase_options)

            val balanceSwitch = findViewById<SwitchCompat>(R.id.payment_balance)
            val couponSwitch = findViewById<SwitchCompat>(R.id.payment_coupon)

            findViewById<Button>(R.id.payment_proceed)?.setOnClickListener {
                purchase.discount = balanceSwitch!!.isChecked

                if (couponSwitch!!.isChecked) {
                    // TODO: Implement coupon db and use it to apply coupons
                    // CouponRepository().applyCoupon(purchase)
                    purchase.coupon = coupons[0].uuid
                }

                PurchaseQRDialogFragment(purchase).show(
                    activity.supportFragmentManager,
                    "PurchaseQRDialogFragment"
                )
                dismiss()
            }


            if (ClientActivity.user.accumulated_value == 0.0) {
                balanceSwitch?.isEnabled = false
            }
            if (coupons.isEmpty()) {
                couponSwitch?.isEnabled = false
            }
            findViewById<TextView>(R.id.payment_total)?.text =
                context.getString(R.string.price_format, purchase.getTotalPrice())
            findViewById<TextView>(R.id.payment_current_balance_value)?.text =
                context.getString(R.string.price_format, ClientActivity.user.accumulated_value)
            findViewById<TextView>(R.id.payment_current_coupons_value)?.text =
                context.getString(R.string.payment_current_coupons_number, coupons.size)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        return PurchaseOptionsDialog(
            requireActivity(), purchase
        )
    }
}