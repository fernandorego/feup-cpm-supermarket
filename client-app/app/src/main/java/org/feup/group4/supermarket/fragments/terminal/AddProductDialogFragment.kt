package org.feup.group4.supermarket.fragments.terminal

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import org.feup.group4.supermarket.R

typealias AddProductListener = (String, Double, DismissCallback) -> Unit
typealias DismissCallback = () -> Unit

class AddProductDialogFragment(private val listener: AddProductListener) :
    AppCompatDialogFragment() {

    class AddProductDialog(context: Context, private val listener: AddProductListener) :
        AppCompatDialog(context) {
        private val productName by lazy { findViewById<android.widget.EditText>(R.id.product_name) }
        private val productPrice by lazy { findViewById<android.widget.EditText>(R.id.product_cost) }
        private val cancelButton by lazy { findViewById<android.widget.Button>(R.id.cancel_button) }
        private val addButton by lazy { findViewById<android.widget.Button>(R.id.add_button) }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setCancelable(false)
            setTitle("Generate Product")
            setContentView(R.layout.dialog_add_product)

            window?.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

            cancelButton?.setOnClickListener {
                dismiss()
            }

            addButton?.setOnClickListener {
                val name = productName!!.text.toString()
                val price = productPrice!!.text.toString().toDoubleOrNull()

                if (price == null || name.isEmpty()) {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.invalid_product_fields),
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }

                listener(name, price) {
                    dismiss()
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        return AddProductDialog(
            activity as Context, listener
        )
    }
}
