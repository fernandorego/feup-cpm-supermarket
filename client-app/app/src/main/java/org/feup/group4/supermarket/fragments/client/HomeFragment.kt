package org.feup.group4.supermarket.fragments.client

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.activities.LoginActivity
import org.feup.group4.supermarket.activities.MainActivity
import org.feup.group4.supermarket.activities.client.ClientActivity
import org.feup.group4.supermarket.activities.client.PurchaseActivity
import org.feup.group4.supermarket.activities.terminal.TerminalActivity
import org.feup.group4.supermarket.adapters.CouponsAdapter
import org.feup.group4.supermarket.adapters.coupons
import org.feup.group4.supermarket.model.Coupon
import org.feup.group4.supermarket.model.User
import org.feup.group4.supermarket.service.AuthService
import org.feup.group4.supermarket.service.UserService
import kotlin.concurrent.thread

class HomeFragment : Fragment() {
    private val user: User = ClientActivity.user
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_client_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val homeHelloTv: TextView = view.findViewById(R.id.home_text_hello)
        homeHelloTv.text = getString(R.string.home_hello, this.user.name)

        val homeBalanceValueTv: TextView = view.findViewById(R.id.home_text_balance_value)
        homeBalanceValueTv.text = getString(
            R.string.price_format,
            this.user.accumulated_value
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.home_coupons_list)
        val emptyRecyclerView = view.findViewById<TextView>(R.id.empty_recyclerview)

        coupons.clear()
        if (!user.active_coupons.isNullOrEmpty()) {
            coupons.addAll(user.active_coupons)
        }

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

        val swipe:SwipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        swipe.setOnRefreshListener {
            thread(start = true) {
                activity?.let { UserService(it.applicationContext, ::updateUser).getUser() }
                swipe.isRefreshing = false
            }
        }
    }

    private fun updateUser(statusCode: Int, json: String?) {
        if (statusCode != 200) {
            activity?.runOnUiThread {
                Toast.makeText(
                    requireContext(),
                    "code: $statusCode\nmessage: $json",
                    Toast.LENGTH_SHORT
                ).show()
            }
            activity?.startActivity(Intent(requireContext(), LoginActivity::class.java))
            activity?.finish()
            return
        }

        ClientActivity.user = Gson().fromJson(json, User::class.java)
        val adapter = view?.findViewById<RecyclerView>(R.id.home_coupons_list)?.adapter as CouponsAdapter
        adapter.setCoupons(ClientActivity.user.active_coupons as ArrayList<Coupon>)
        activity?.runOnUiThread {
            adapter.notifyDataSetChanged()
        }
    }
}