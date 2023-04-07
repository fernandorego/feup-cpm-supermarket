package org.feup.group4.supermarket.adapters

import android.app.Activity
import android.content.Context
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.model.Product

class ProductsAdapter(
    private val activity: Activity,
    private val products: ArrayList<Pair<Product, Int>>,
    private val onChangeCallBack: (Int) -> Unit = {},
    private val showQuantity: Boolean = true
) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = activity.layoutInflater.inflate(R.layout.recyclerview_product, parent, false)

        val removeItemButton = view.findViewById<ImageButton>(R.id.item_remove)
        removeItemButton.setOnClickListener {
            for (i in 0 until products.size) {
                if (products[i].first.name == view.findViewById<TextView>(R.id.item_name).text) {
                    products.removeAt(i)
                    notifyItemRemoved(i)
                    onChangeCallBack(i)
                    break
                }
            }
        }

        if (!showQuantity) {
            view.findViewById<EditText>(R.id.item_quantity).visibility = View.GONE
            view.findViewById<TextView>(R.id.item_units).visibility = View.GONE
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(products[position]) {
            holder.name.text = first.name
            holder.price.text = activity.getString(
                R.string.price_format,
                first.euros,
                first.cents
            )
            holder.quantity.text = second.toString()
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