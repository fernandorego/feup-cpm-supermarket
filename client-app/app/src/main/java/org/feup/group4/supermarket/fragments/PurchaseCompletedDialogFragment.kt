package org.feup.group4.supermarket.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import org.feup.group4.supermarket.R

class PurchaseCompletedDialogFragment : AppCompatDialogFragment() {
    class PurchaseCompletedDialog(
        context: Context, private val success: Boolean, private val paidPrice: Double
    ) : AppCompatDialog(context) {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setCancelable(false)
            setTitle(
                context.resources.getString(
                    if (success) R.string.purchase_success_message else R.string.purchase_error_message
                )
            )
            setContentView(R.layout.dialog_purchase_complete)

            val closeButton = findViewById<Button>(R.id.close_button)
            closeButton?.setOnClickListener {
                dismiss()
            }

            val paidPriceText = findViewById<android.widget.TextView>(R.id.purchase_paid_text)
            if (!success) {
                val imageView = findViewById<ImageView>(R.id.hint_image)
                imageView?.setImageResource(R.drawable.error_done)
                imageView?.setColorFilter(
                    context.resources.getColor(
                        R.color.dark_red, context.theme
                    )
                )
                paidPriceText?.visibility = android.view.View.GONE
            } else {
                paidPriceText?.text = buildString {
                    append(paidPriceText?.text.toString() + " ")
                    append(
                        context.getString(
                            R.string.price_format, paidPrice
                        )
                    )
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        return PurchaseCompletedDialog(
            activity as Context, arguments?.getBoolean("success") ?: false, arguments?.getDouble(
                "paidPrice"
            ) ?: 0.0
        )
    }

    companion object {
        fun newInstance(success: Boolean, paidPrice: Double): PurchaseCompletedDialogFragment {
            val bundle = Bundle()
            bundle.putBoolean("success", success)
            bundle.putDouble("paidPrice", paidPrice)
            val fragment = PurchaseCompletedDialogFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}