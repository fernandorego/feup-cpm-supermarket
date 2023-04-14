package org.feup.group4.supermarket.activities.client

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.adapters.ProductsAdapter
import org.feup.group4.supermarket.fragments.client.PurchaseOptionsDialogFragment
import org.feup.group4.supermarket.model.Purchase
import org.feup.group4.supermarket.service.ProductService
import org.feup.group4.supermarket.service.QRService
import kotlin.concurrent.thread

private val purchase = Purchase()

class PurchaseActivity : AppCompatActivity() {
    private val qrService = QRService(this)

    private val recyclerView: RecyclerView by lazy { findViewById(R.id.shopping_cart_items) }
    private val emptyRecyclerView: TextView by lazy { findViewById(R.id.empty_recyclerview) }
    private val checkoutBtn: ExtendedFloatingActionButton by lazy { findViewById(R.id.shopping_cart_checkout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase)

        val scanButton = findViewById<FloatingActionButton>(R.id.shopping_cart_add)
        scanButton.setOnClickListener {
            qrService.scanQRCode { content ->
                if (content == null) {
                    Toast.makeText(
                        this, resources.getString(R.string.scan_qr_error), Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this, "Scanned: $content", Toast.LENGTH_LONG
                    ).show()
                    val product = ProductService(this).decryptProduct(content)
                    val res = purchase.addProduct(product)

                    if (res.first == purchase.getProducts().size - 1) {
                        recyclerView.adapter?.notifyItemInserted(res.first)
                    } else {
                        recyclerView.adapter?.notifyItemChanged(res.first)
                    }

                    if (res.second.image.isEmpty()) {
                        thread(start = true) {
                            ProductService(this).getProductImage(product.uuid!!) {
                                res.second.image = it
                                recyclerView.adapter?.notifyItemChanged(res.first)
                            }
                        }
                    }

                }
            }
        }

        checkoutBtn.setOnClickListener {
            PurchaseOptionsDialogFragment(purchase).show(
                supportFragmentManager,
                "PurchaseOptionsDialogFragment"
            )
        }
    }

    override fun onResume() {
        super.onResume()

        updateView()

        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ProductsAdapter(this, purchase.getProducts(), { updateView() })
        recyclerView.adapter = adapter
    }

    private fun updateView() {
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
    }

    private fun updateSubTotal() {
        val totalValueTextView = findViewById<TextView>(R.id.shopping_cart_subtotal_value)
        totalValueTextView.text = getString(R.string.price_format, purchase.getTotalPrice())
    }
}