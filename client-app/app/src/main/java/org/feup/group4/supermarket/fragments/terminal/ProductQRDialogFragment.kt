package org.feup.group4.supermarket.fragments.terminal

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
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.model.Product
import org.feup.group4.supermarket.service.QRService
import java.io.File
import java.io.FileOutputStream

class ProductQRDialogFragment(private val product: Product) : AppCompatDialogFragment() {
    class ProductQRDialog(context: Context, private val product: Product) :
        AppCompatDialog(context) {
        private fun setQRImage(): Bitmap {
            // TODO: Add proper QR code payload with encryption
            val contents = product.toString()
            val bitmap = QRService.generateQRCode(contents, 600, 600)
            val qrCodeImage = findViewById<ImageView>(R.id.qr_image)
            qrCodeImage?.setImageBitmap(bitmap)
            return bitmap
        }

        private fun exportImage(bitmap: Bitmap) {
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

            val bitmap = setQRImage()

            val closeButton = findViewById<Button>(R.id.close_button)
            closeButton?.setOnClickListener {
                dismiss()
            }

            val exportButton = findViewById<Button>(R.id.export_button)
            exportButton?.setOnClickListener {
                try {
                    exportImage(bitmap)
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
            activity as Context, product
        )
    }
}