package org.feup.group4.supermarket.fragments.terminal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.adapters.ProductsAdapter
import org.feup.group4.supermarket.model.Product

private val products = ArrayList<Pair<Product, Int>>()

class ProductsFragment : Fragment() {
    // private val user: User = TerminalActivity.user
    private lateinit var adapter: ProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_terminal_products, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.home_products_list)
        val emptyRecyclerView = view.findViewById<TextView>(R.id.empty_recyclerview)

        if (products.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyRecyclerView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyRecyclerView.visibility = View.GONE
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        this.adapter = ProductsAdapter(requireContext(), products, adminMode = true)
        recyclerView.adapter = adapter

        val newProductButton = view.findViewById<TextView>(R.id.new_product_fab)
        newProductButton.setOnClickListener {
            AddProductDialogFragment(::addProduct).show(childFragmentManager, "AddProductDialogFragment")
        }
    }

    private fun addProduct(productName: String, productPrice: Double, successCallBack: DismissCallback) {
        // TODO: Add product to database and verify correctness
        val product = Product(
            productName,
            (productPrice / 100).toInt(),
            ((productPrice * 100) % 100).toInt(),
        )
        products.add(Pair(product, 1))
        adapter.notifyItemInserted(products.size - 1)
        println("Added product $productName with price $productPrice")
        println("Products: $products")
        successCallBack()
    }
}