package org.feup.group4.supermarket.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import org.feup.group4.supermarket.R

class PurchaseCompletedDialogFragment(private val success: Boolean) : AppCompatDialogFragment() {
    class PurchaseCompletedDialog(context: Context, private val success: Boolean) :
        AppCompatDialog(context) {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setCancelable(true)
            setTitle(
                context.resources.getString(
                    if (success) R.string.purchase_success_message else R.string.purchase_error_message
                )
            )
            setContentView(R.layout.dialog_purchase_complete)

            if (!success) {
                val imageView = findViewById<ImageView>(R.id.hint_image)
                imageView?.setImageResource(R.drawable.error_done)
                imageView?.setColorFilter(context.resources.getColor(R.color.dark_red, context.theme))
            }

            window?.decorView?.postDelayed(
                {
                    dismiss()
                }, 5000
            )
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        return PurchaseCompletedDialog(
            activity as Context, success
        )
    }
}