package org.feup.group4.supermarket.adapters

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.model.Product

class ProductsAdapter(private val ctx: Context, private val products: LinkedHashMap<Product, Int>): RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = (ctx as Activity).layoutInflater.inflate(R.layout.recyclerview_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(products.keys.elementAt(position)) {
            holder.name.text = name
            holder.price.text = ctx.getString(
                R.string.price_format,
                euros,
                cents)
            holder.quantity.text = products[this].toString()
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val name: TextView = itemView.findViewById(R.id.item_name)
        val price: TextView = itemView.findViewById(R.id.item_price)
        val quantity: TextView = itemView.findViewById(R.id.item_quantity)
    }
}