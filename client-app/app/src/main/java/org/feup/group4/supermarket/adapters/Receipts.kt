package org.feup.group4.supermarket.adapters

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.model.Receipt

val receipts = ArrayList<Receipt>()

class ReceiptsAdapter(private val ctx: Context, private val receipts: ArrayList<Receipt>): RecyclerView.Adapter<ReceiptsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = (ctx as Activity).layoutInflater.inflate(R.layout.recyclerview_receipt, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(receipts[position]) {
//            holder.receiptDate.text = ctx.getString(
//                R.string.date_format_with_year,
//                date.month.name.lowercase().replaceFirstChar { it.uppercaseChar() },
//                date.dayOfMonth,
//                date.year)
            holder.receiptTotal.text = ctx.getString(R.string.price_format, total)
        }
    }

    override fun getItemCount(): Int {
        return receipts.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
//        val receiptDate: TextView = itemView.findViewById(R.id.receipt_tv_date)
        val receiptTotal: TextView = itemView.findViewById(R.id.receipt_tv_total)
    }
}