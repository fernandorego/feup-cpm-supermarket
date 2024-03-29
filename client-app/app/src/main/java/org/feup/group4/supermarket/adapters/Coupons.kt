package org.feup.group4.supermarket.adapters

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.model.Coupon

val coupons = ArrayList<Coupon>()

class CouponsAdapter(private val ctx: Context, val coupons: ArrayList<Coupon>): RecyclerView.Adapter<CouponsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = (ctx as Activity).layoutInflater.inflate(R.layout.recyclerview_client_coupon, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {}

    override fun getItemCount(): Int = coupons.size

    fun setCoupons(newCoupons:ArrayList<Coupon>) {
        coupons.clear()
        coupons.addAll(newCoupons)
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView)
}