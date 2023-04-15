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

class AddProductDialogFragment :
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
            setTitle(context.resources.getString(R.string.products_new_product))
            setContentView(R.layout.dialog_add_product)

            cancelButton?.setOnClickListener {
                dismiss()
            }

            addButton?.setOnClickListener {
                addProduct()
            }
        }

        private fun addProduct() {
            val name = productName!!.text.toString()
            val price = productPrice!!.text.toString().toDoubleOrNull()

            if (price == null || name.isEmpty() || price <= 0) {
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.invalid_product_fields),
                    Toast.LENGTH_LONG
                ).show()
                return
            }

            listener(name, price) {
                dismiss()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        return AddProductDialog(
            activity as Context, arguments?.getSerializable("listener") as AddProductListener
        )
    }

    companion object {
        fun newInstance(listener: AddProductListener): AddProductDialogFragment {
            val bundle = Bundle()
            bundle.putSerializable("listener", listener as java.io.Serializable)
            val fragment = AddProductDialogFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
