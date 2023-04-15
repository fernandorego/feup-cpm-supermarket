package org.feup.group4.supermarket.adapters

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.model.Product

class ReceiptProductsAdapter(private val ctx: Context, private val products: ArrayList<Product>): RecyclerView.Adapter<ReceiptProductsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = (ctx as Activity).layoutInflater.inflate(R.layout.recyclerview_receipt_cart, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(products[position]) {
            holder.productTitle.text = ctx.getString(
                R.string.cart_product_format, quantity, name)
            holder.productPrice.text = ctx.getString(R.string.price_format, price * quantity)
        }
    }

    override fun getItemCount(): Int = products.size

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val productTitle: TextView = itemView.findViewById(R.id.cart_tv_title)
        val productPrice: TextView = itemView.findViewById(R.id.cart_tv_price)
    }
}