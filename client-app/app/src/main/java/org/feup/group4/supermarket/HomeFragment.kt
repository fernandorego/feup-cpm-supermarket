package org.feup.group4.supermarket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.home_coupons_list)
        val emptyRecyclerView = view.findViewById<TextView>(R.id.empty_recyclerview)

        if (coupons.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyRecyclerView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyRecyclerView.visibility = View.GONE
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapter = CouponsAdapter(requireContext(), coupons)
        recyclerView.adapter = adapter
    }
}