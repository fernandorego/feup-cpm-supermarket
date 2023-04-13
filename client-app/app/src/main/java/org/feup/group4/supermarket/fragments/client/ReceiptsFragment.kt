package org.feup.group4.supermarket.fragments.client

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
import org.feup.group4.supermarket.activities.client.PurchaseActivity
import org.feup.group4.supermarket.adapters.ReceiptsAdapter
import org.feup.group4.supermarket.model.Receipt
import org.feup.group4.supermarket.service.PurchaseService
import java.time.Instant
import java.time.ZoneId
import kotlin.concurrent.thread

private val receipts = ArrayList<Receipt>()

class ReceiptsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_client_receipts, container, false)

    private val recyclerView: RecyclerView by lazy { view?.findViewById(R.id.receipts_list)!! }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        thread(start=true) {
            PurchaseService(requireContext()).getPurchases { purchases ->
                requireActivity().runOnUiThread {
                    receipts.clear()
                    purchases.forEach { purchase ->
                        receipts.add(Receipt(Instant.parse(purchase.created_at).atZone(ZoneId.systemDefault()).toLocalDate(), purchase.total_price))
                    }
                    updateListVisibility()
                }
            }
        }

        updateListVisibility()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ReceiptsAdapter(requireContext(), receipts)
        recyclerView.adapter = adapter

        val newPurchaseButton = view.findViewById<TextView>(R.id.empty_recyclerview)
        newPurchaseButton.setOnClickListener {
            startActivity(Intent(activity, PurchaseActivity::class.java))
        }
    }

    private fun updateListVisibility() {
        val emptyRecyclerView = view?.findViewById<TextView>(R.id.empty_recyclerview)

        if (receipts.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyRecyclerView?.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyRecyclerView?.visibility = View.GONE
        }
    }
}