package org.feup.group4.supermarket.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.activities.MainActivity
import org.feup.group4.supermarket.activities.client.ClientActivity
import org.feup.group4.supermarket.activities.terminal.PurchaseActivity
import org.feup.group4.supermarket.adapters.CouponsAdapter
import org.feup.group4.supermarket.adapters.coupons
import org.feup.group4.supermarket.model.User

class ClientHomeFragment : Fragment() {
    private val user: User = ClientActivity.user
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_client_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val homeHelloTv: TextView = view.findViewById(R.id.home_text_hello)
        homeHelloTv.text = getString(R.string.home_hello, this.user.name)

        val homeBalanceValueTv: TextView = view.findViewById(R.id.home_text_balance_value)
        val accumulatedValueEuros = this.user.accumulated_value?.div(100)?.toInt() ?: 0
        val accumulatedValueCents = this.user.accumulated_value?.times(100)?.rem(100)?.toInt() ?: 0
        homeBalanceValueTv.text = getString(
            R.string.price_format,
            accumulatedValueEuros, accumulatedValueCents
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.home_coupons_list)
        val emptyRecyclerView = view.findViewById<TextView>(R.id.empty_recyclerview)

        if (coupons.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyRecyclerView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyRecyclerView.visibility = View.GONE
        }

        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapter = CouponsAdapter(requireContext(), coupons)
        recyclerView.adapter = adapter

        val newPurchaseButton = view.findViewById<TextView>(R.id.home_new_purchase)
        newPurchaseButton.setOnClickListener {
            startActivity(Intent(activity, PurchaseActivity::class.java))
        }
    }
}