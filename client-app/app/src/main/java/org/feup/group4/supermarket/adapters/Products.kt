package org.feup.group4.supermarket.adapters

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.fragments.terminal.ProductQRDialogFragment
import org.feup.group4.supermarket.model.Product
import org.feup.group4.supermarket.service.ProductService
import kotlin.concurrent.thread

class ProductsAdapter(
    private val context: Context,
    private val products: ArrayList<Pair<Product, Int>>,
    private val onChangeCallBack: (Int) -> Unit = {},
    private val adminMode: Boolean = false
) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = (context as Activity).layoutInflater.inflate(
            R.layout.recyclerview_product,
            parent,
            false
        )

        val removeItemButton = view.findViewById<ImageButton>(R.id.item_remove)
        removeItemButton.setOnClickListener {
            if (adminMode) {
                AlertDialog.Builder(context)
                    .setTitle(context.resources.getString(R.string.delete_product))
                    .setMessage(context.resources.getString(R.string.delete_product_message))
                    .setPositiveButton(context.resources.getString(R.string.yes)) { _, _ ->
                        thread(start = true) {
                            for (i in 0 until products.size) {
                                if (products[i].first.name == view.findViewById<TextView>(R.id.item_name).text) {
                                    ProductService(context).deleteProduct(products[i].first) {
                                        context.runOnUiThread {
                                            removeItem(view)
                                        }
                                    }
                                    break
                                }
                            }
                        }
                    }
                    .setNegativeButton(
                        context.resources.getString(R.string.no), null
                    ).show()
                return@setOnClickListener
            }
            removeItem(view)
        }

        val generateQRButton = view.findViewById<ImageButton>(R.id.item_generate_qr)
        generateQRButton.setOnClickListener {
            val product =
                products.first { it.first.name == view.findViewById<TextView>(R.id.item_name).text }.first
            ProductQRDialogFragment(product).show(
                (context as AppCompatActivity).supportFragmentManager,
                "ProductQRDialogFragment"
            )
        }

        val addQuantity = view.findViewById<ImageButton>(R.id.quantity_add)
        addQuantity.setOnClickListener {
            for (i in 0 until products.size) {
                if (products[i].first.name == view.findViewById<TextView>(R.id.item_name).text) {
                    addQuantity(i)
                    break
                }
            }
        }

        val rmQuantity = view.findViewById<ImageButton>(R.id.quantity_remove)
        rmQuantity.setOnClickListener {
            for (i in 0 until products.size) {
                if (products[i].first.name == view.findViewById<TextView>(R.id.item_name).text) {
                    removeQuantity(i)
                    break
                }
            }
        }


        if (adminMode) {
            rmQuantity.visibility = View.GONE
            addQuantity.visibility = View.GONE
            view.findViewById<TextView>(R.id.item_quantity).visibility = View.GONE
            view.findViewById<TextView>(R.id.item_units).visibility = View.GONE
        } else {
            generateQRButton.visibility = View.GONE
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(products[position]) {
            holder.name.text = first.name
            holder.price.text = context.getString(
                R.string.price_format,
                first.price
            )
            holder.quantity.text = second.toString()

            if (second == 1) {
                holder.itemView.findViewById<ImageButton>(R.id.quantity_remove).visibility =
                    View.INVISIBLE
            } else {
                holder.itemView.findViewById<ImageButton>(R.id.quantity_remove).visibility =
                    View.VISIBLE
            }

            if (first.image != "") {
                val imageBytes = Base64.decode(first.image, Base64.DEFAULT)
                holder.image.setImageBitmap(
                    BitmapFactory.decodeByteArray(
                        imageBytes,
                        0,
                        imageBytes.size
                    )
                )
                holder.image.imageTintList = null
            }
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    private fun removeItem(view: View) {
        for (i in 0 until products.size) {
            if (products[i].first.name == view.findViewById<TextView>(R.id.item_name).text) {
                products.removeAt(i)
                notifyItemRemoved(i)
                onChangeCallBack(i)
                break
            }
        }
    }

    private fun addQuantity(i: Int) {
        products[i] = products[i].copy(second = products[i].second + 1)
        notifyItemChanged(i)
        onChangeCallBack(i)
    }

    private fun removeQuantity(i: Int) {
        if (products[i].second > 1) {
            products[i] = products[i].copy(second = products[i].second - 1)
            notifyItemChanged(i)
            onChangeCallBack(i)
        }
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val name: TextView = itemView.findViewById(R.id.item_name)
        val price: TextView = itemView.findViewById(R.id.item_price)
        val quantity: TextView = itemView.findViewById(R.id.item_quantity)
        val image: ImageView = itemView.findViewById(R.id.item_image)
    }
}