package org.feup.group4.supermarket.fragments.terminal

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.model.Product
import org.feup.group4.supermarket.service.ProductService
import org.feup.group4.supermarket.service.QRService
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread

class ProductQRDialogFragment(private val product: Product) : AppCompatDialogFragment() {
    class ProductQRDialog(private val activity: FragmentActivity, private val product: Product) :
        AppCompatDialog(activity) {
        private lateinit var bitmap: Bitmap
        private fun setQRImage() {
            thread(start = true) {
                var contents = ""
                ProductService(context).getEncryptedProduct(product.uuid!!) {
                    contents = it
                }
                bitmap = QRService.generateQRCode(contents, 600, 600)
                activity.runOnUiThread {
                    val qrCodeImage = findViewById<ImageView>(R.id.qr_image)
                    qrCodeImage?.setImageBitmap(bitmap)
                }
            }
        }

        private fun exportImage() {
            val directory = File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS
                ),
                context.resources.getString(R.string.app_name)
            )
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val output = FileOutputStream(
                File(
                    directory,
                    product.name + "_qr.jpeg"
                )
            )

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
            output.flush()
            output.close()

            Toast.makeText(
                context,
                context.resources.getString(R.string.export_file_success),
                Toast.LENGTH_LONG
            ).show()
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setCancelable(false)
            setTitle(product.name)
            setContentView(R.layout.dialog_product_qr)

            setQRImage()

            val closeButton = findViewById<Button>(R.id.close_button)
            closeButton?.setOnClickListener {
                dismiss()
            }

            val exportButton = findViewById<Button>(R.id.export_button)
            exportButton?.setOnClickListener {
                try {
                    exportImage()
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.export_file_failed),
                        Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        return ProductQRDialog(
            requireActivity(), product
        )
    }
}