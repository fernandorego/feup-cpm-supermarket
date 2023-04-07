package org.feup.group4.supermarket.activities.terminal

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.adapters.ProductsAdapter
import org.feup.group4.supermarket.model.Product
import org.feup.group4.supermarket.model.Purchase

private val purchase = Purchase()

class PurchaseActivity : AppCompatActivity() {
    private val adapter = ProductsAdapter(this, purchase.getProducts())

    private val recyclerView: RecyclerView by lazy { findViewById(R.id.shopping_cart_items) }
    private val emptyRecyclerView: TextView by lazy { findViewById(R.id.empty_recyclerview) }
    private val checkoutBtn: ExtendedFloatingActionButton by lazy { findViewById(R.id.shopping_cart_checkout) }

    private val qrCodeScannerLauncher: ActivityResultLauncher<ScanOptions> =
        registerForActivityResult(
            ScanContract()
        ) { result ->
            if (result.contents == null) {
                Toast.makeText(this, resources.getString(R.string.scan_qr_error), Toast.LENGTH_LONG)
                    .show()
            } else {
                // TODO: Decrypt QR code and add actual product
                Toast.makeText(
                    this,
                    "Scanned: " + result.contents,
                    Toast.LENGTH_LONG
                ).show()
                purchase.addProduct(Product("Test", 1, 0))
                purchase.addProduct(Product("Test2", 1, 0))
                purchase.addProduct(Product("Test3", 1, 0))
                purchase.addProduct(Product("Test4", 1, 0))
                purchase.addProduct(Product("Test5", 1, 0))
                adapter.notifyItemInserted(purchase.getProducts().size - 1)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase)

        val scanButton = findViewById<FloatingActionButton>(R.id.shopping_cart_add)
        scanButton.setOnClickListener {
            val scanOptions = ScanOptions()
            scanOptions.setOrientationLocked(false)
            scanOptions.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            scanOptions.setPrompt(resources.getString(R.string.scan_product_qr))
            qrCodeScannerLauncher.launch(scanOptions)
        }
    }

    override fun onResume() {
        super.onResume()

        updateSubTotal()

        if (purchase.getProducts().isEmpty()) {
            recyclerView.visibility = View.GONE
            checkoutBtn.visibility = View.GONE
            emptyRecyclerView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            checkoutBtn.visibility = View.VISIBLE
            emptyRecyclerView.visibility = View.GONE
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ProductsAdapter(this, purchase.getProducts(), { updateSubTotal() })
        recyclerView.adapter = adapter
    }

    private fun updateSubTotal() {
        val totalValueTextView = findViewById<TextView>(R.id.shopping_cart_subtotal_value)
        val totalValueEuros = purchase.getTotalPrice().toInt()
        val totalValueCents = purchase.getTotalPrice().times(100).rem(100).toInt()
        totalValueTextView.text = getString(R.string.price_format, totalValueEuros, totalValueCents)
    }
}