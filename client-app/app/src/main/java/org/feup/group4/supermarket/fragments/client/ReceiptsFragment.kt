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
import org.feup.group4.supermarket.repository.ReceiptsRepository
import org.feup.group4.supermarket.service.PurchaseService
import kotlin.concurrent.thread

private var receipts = ArrayList<Receipt>()

class ReceiptsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_client_receipts, container, false)

    private val recyclerView: RecyclerView by lazy { view?.findViewById(R.id.receipts_list)!! }
    private val receiptsRepository by lazy { ReceiptsRepository.getInstance(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        thread(start=true) {
            PurchaseService(requireContext()).getReceipts { remoteReceipts ->
                requireActivity().runOnUiThread {
                    receipts.clear()
                    if (remoteReceipts.isEmpty()) {
                        receiptsRepository.getAll().forEach { receipt ->
                            receipts.add(receipt)
                        }
                    } else {
                        remoteReceipts.forEach { receipt ->
                            val receiptInDatabase = receipt.uuid?.let { receiptsRepository.getReceipt(it) }
                            if (receiptInDatabase == null)
                                receiptsRepository.addReceipt(receipt)
                            receipts.add(receipt)
                        }
                    }
                    updateListVisibility()
                }
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ReceiptsAdapter(requireContext(), receipts)
        recyclerView.adapter = adapter

        val newPurchaseButton = view.findViewById<TextView>(R.id.empty_recyclerview)
        newPurchaseButton.setOnClickListener {
            startActivity(Intent(activity, PurchaseActivity::class.java))
        }

        updateListVisibility()
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