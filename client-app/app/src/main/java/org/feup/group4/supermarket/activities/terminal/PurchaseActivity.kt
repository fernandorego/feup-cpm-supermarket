package org.feup.group4.supermarket.activities.terminal

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.adapters.ProductsAdapter
import org.feup.group4.supermarket.model.Purchase


class PurchaseActivity : AppCompatActivity() {
    private val purchase = Purchase()
    private val adapter = ProductsAdapter(this, purchase.products)

    private val recyclerView: RecyclerView by lazy { findViewById(R.id.shopping_cart_items) }
    private val emptyRecyclerView: TextView by lazy { findViewById(R.id.empty_recyclerview) }

    private val qrCodeScannerLauncher: ActivityResultLauncher<ScanOptions> =
        registerForActivityResult(
            ScanContract()
        ) { result ->
            if (result.contents == null) {
                Toast.makeText(this, resources.getString(R.string.scan_qr_error), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    this,
                    "Scanned: " + result.contents,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase)

        if (purchase.products.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyRecyclerView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyRecyclerView.visibility = View.GONE
        }

        recyclerView.adapter = adapter

        val scanButton = findViewById<FloatingActionButton>(R.id.shopping_cart_add)
        scanButton.setOnClickListener {
            val scanOptions = ScanOptions()
            scanOptions.setOrientationLocked(false)
            scanOptions.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            scanOptions.setPrompt(resources.getString(R.string.scan_product_qr))
            qrCodeScannerLauncher.launch(scanOptions)
        }
    }

    override fun onStart() {
        super.onStart()
        val totalValueTextView = findViewById<TextView>(R.id.shopping_cart_subtotal_value)
        val totalValueEuros = purchase.getTotalPrice().div(100).toInt()
        val totalValueCents = purchase.getTotalPrice().times(100).rem(100).toInt()
        totalValueTextView.text = getString(R.string.price_format, totalValueEuros, totalValueCents)
    }
}