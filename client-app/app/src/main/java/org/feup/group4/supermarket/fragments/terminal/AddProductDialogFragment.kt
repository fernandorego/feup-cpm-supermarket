package org.feup.group4.supermarket.fragments.terminal

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker
import org.feup.group4.supermarket.R

typealias AddProductListener = (String, Double, ByteArray, DismissCallback) -> Unit
typealias DismissCallback = () -> Unit

class AddProductDialogFragment(private val listener: AddProductListener) :
    AppCompatDialogFragment() {
    var dialog: AddProductDialog? = null

    class AddProductDialog(private val fragment: Fragment, private val listener: AddProductListener) :
        AppCompatDialog(fragment.requireContext()) {
        private val productName by lazy { findViewById<android.widget.EditText>(R.id.product_name) }
        private val productPrice by lazy { findViewById<android.widget.EditText>(R.id.product_cost) }
        private val cancelButton by lazy { findViewById<android.widget.Button>(R.id.cancel_button) }
        private val addButton by lazy { findViewById<android.widget.Button>(R.id.add_button) }
        private val image by lazy { findViewById<ImageView>(R.id.product_image) }
        private val addImage by lazy { findViewById<ImageView>(R.id.product_add_image) }
        private var imageURI: android.net.Uri? = null

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

            addImage?.setOnClickListener {
                ImagePicker.with(fragment)
                    .cropSquare()
                    .maxResultSize(512, 512)
                    .compress(4096)
                    .start(1)
            }
        }

        fun setProductImage(imageURI: android.net.Uri) {
            this.imageURI = imageURI
            image?.setImageURI(imageURI)
            image?.imageTintList = null
        }

        private fun addProduct() {
            val name = productName!!.text.toString()
            val price = productPrice!!.text.toString().toDoubleOrNull()
            var imageBytes = ByteArray(0)
            if (imageURI != null) {
                val inputStream = imageURI?.let { context.contentResolver.openInputStream(it) }
                imageBytes = inputStream?.buffered().use { it!!.readBytes() }
                inputStream?.close()
            }

            if (price == null || name.isEmpty() || price <= 0) {
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.invalid_product_fields),
                    Toast.LENGTH_LONG
                ).show()
                return
            }

            listener(name, price, imageBytes) {
                dismiss()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        dialog = AddProductDialog(this, listener)
        return dialog!!
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            // Uri object will not be null for RESULT_OK
            if (requestCode == 1){
                data?.data?.let { dialog?.setProductImage(it) }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}