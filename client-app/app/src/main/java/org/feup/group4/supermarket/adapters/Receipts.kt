package org.feup.group4.supermarket.adapters

import android.app.Activity
import android.content.Context
import android.text.format.DateFormat
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.model.Receipt
import java.time.ZonedDateTime
import java.util.*
import kotlin.collections.ArrayList

class ReceiptsAdapter(private val ctx: Context, private val receipts: ArrayList<Receipt>): RecyclerView.Adapter<ReceiptsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = (ctx as Activity).layoutInflater.inflate(R.layout.recyclerview_receipt, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(receipts[position]) {
            val date = Date.from(ZonedDateTime.parse(created_at).toInstant())

            holder.receiptDate.text = DateFormat.getMediumDateFormat(ctx).format(date)
            holder.receiptTotal.text = ctx.getString(R.string.price_format, total_price)

            holder.receiptProducts.layoutManager = LinearLayoutManager(ctx)
            val adapter = ReceiptProductsAdapter(ctx, cart)
            holder.receiptProducts.adapter = adapter

            if (cart.isNotEmpty()) {
                holder.expandProducts.visibility = View.VISIBLE
            }
            holder.expandProducts.setOnClickListener {
                if (holder.receiptProducts.visibility == View.VISIBLE) {
                    holder.receiptProducts.visibility = View.GONE
                    holder.expandProducts.animate().rotation(0f)
                        .setDuration(150).setInterpolator(AccelerateDecelerateInterpolator())
                } else {
                    holder.receiptProducts.visibility = View.VISIBLE
                    holder.expandProducts.animate().rotation(180f)
                        .setDuration(150).setInterpolator(AccelerateDecelerateInterpolator())
                }
            }
            holder.background.setOnClickListener { holder.expandProducts.callOnClick() }
        }
    }

    override fun getItemCount(): Int = receipts.size

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val receiptDate: TextView = itemView.findViewById(R.id.receipt_tv_date)
        val receiptTotal: TextView = itemView.findViewById(R.id.receipt_tv_total)
        val receiptProducts: RecyclerView = itemView.findViewById(R.id.receipt_rv_products_list)
        val expandProducts: ImageButton = itemView.findViewById(R.id.expand_btn)
        val background: ConstraintLayout = itemView.findViewById(R.id.receipt_cl_background)
    }
}