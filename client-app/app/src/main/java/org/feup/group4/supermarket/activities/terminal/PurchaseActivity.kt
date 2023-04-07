package org.feup.group4.supermarket.activities.terminal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.adapters.ProductsAdapter
import org.feup.group4.supermarket.model.Purchase

class PurchaseActivity : AppCompatActivity() {
    private val purchase = Purchase()

    private val recyclerView: RecyclerView by lazy {findViewById(R.id.shopping_cart_items)}
    private val emptyRecyclerView: TextView by lazy {findViewById(R.id.empty_recyclerview)}

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

        val adapter = ProductsAdapter(this, purchase.products)
        recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        val totalValueTextView = findViewById<TextView>(R.id.shopping_cart_subtotal_value)
        val totalValueEuros = purchase.getTotalPrice().div(100).toInt()
        val totalValueCents = purchase.getTotalPrice().times(100).rem(100).toInt()
        totalValueTextView.text = getString(R.string.price_format, totalValueEuros, totalValueCents)
    }
}