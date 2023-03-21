package org.feup.group4.supermarket

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

data class Coupon(
    var expiration: LocalDate
)

var coupons = ArrayList<Coupon>()

class CouponsAdapter(private val ctx: Context, private val coupons: ArrayList<Coupon>): RecyclerView.Adapter<CouponsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = (ctx as Activity).layoutInflater.inflate(R.layout.recyclerview_coupon, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(coupons[position]) {
            holder.dueDate.text = ctx.getString(R.string.date_format,
                expiration.month.toString().substring(0,3), expiration.dayOfMonth)
        }
    }

    override fun getItemCount(): Int {
        return coupons.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val dueDate: TextView = itemView.findViewById(R.id.tv_duedate)
    }
}