package org.feup.group4.supermarket.fragments.terminal

import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.adapters.ProductsAdapter
import org.feup.group4.supermarket.model.Product
import org.feup.group4.supermarket.repository.ProductsRepository
import org.feup.group4.supermarket.service.ProductService
import kotlin.concurrent.thread

private val products = ArrayList<Pair<Product, Int>>()

class ProductsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_terminal_products, container, false)

    private val productRepository by lazy { ProductsRepository.getInstance(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.home_products_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ProductsAdapter(requireContext(), products, { updateListVisibility() }, true)
        recyclerView.adapter = adapter

        val newProductButton = view.findViewById<TextView>(R.id.new_product_fab)
        newProductButton.setOnClickListener {
            AddProductDialogFragment { name, title, imageBytes, callback ->
                addProduct(
                    name,
                    title,
                    imageBytes,
                    adapter,
                    callback
                )
            }.show(
                childFragmentManager,
                "AddProductDialogFragment"
            )
        }

        getProducts()

        updateListVisibility()

        val swipe: SwipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        swipe.setOnRefreshListener {
            thread(start = true) {
                getProducts()
                swipe.isRefreshing = false
            }
        }
    }

    private fun getProducts() {
        thread(start = true) {
            ProductService(requireContext()).getProducts { remoteProducts ->
                requireActivity().runOnUiThread {
                    products.clear()
                    if (remoteProducts.isEmpty()) {
                        productRepository.getAll().forEach { product ->
                            products.add(Pair(product, 1))
                        }
                    } else {
                        remoteProducts.forEach { remoteProduct ->
                            val productInDatabase = productRepository.getProduct(remoteProduct.name)
                            if (productInDatabase == null) {
                                productRepository.addProduct(remoteProduct)
                            } else if (productInDatabase != remoteProduct) {
                                productRepository.updateProduct(remoteProduct)
                            }
                            products.add(Pair(remoteProduct, 1))
                        }
                    }
                    updateListVisibility()
                }
            }
        }
    }

    private fun updateListVisibility() {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.home_products_list)
        val emptyRecyclerView = view?.findViewById<TextView>(R.id.empty_recyclerview)

        if (products.isEmpty()) {
            recyclerView?.visibility = View.GONE
            emptyRecyclerView?.visibility = View.VISIBLE
        } else {
            recyclerView?.visibility = View.VISIBLE
            emptyRecyclerView?.visibility = View.GONE
        }
    }

    private fun addProduct(
        productName: String,
        productPrice: Double,
        imageBytes: ByteArray,
        adapter: ProductsAdapter,
        successCallBack: DismissCallback
    ) {
        val product = Product(
            productName,
            productPrice,
            image = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        )

        thread(start = true) {
            ProductService(requireContext()).createReplaceProduct(product) {
                requireActivity().runOnUiThread {
                    val productInDatabase = productRepository.getProduct(it.name)

                    if (productInDatabase == null) {
                        productRepository.addProduct(it)
                        products.add(Pair(it, 1))
                        adapter.notifyItemInserted(products.size - 1)
                    } else if (productInDatabase != it) {
                        productRepository.updateProduct(it)
                        products[productRepository.getAll().indexOf(it)] = Pair(it, 1)
                        adapter.notifyItemChanged(productRepository.getAll().indexOf(it))
                    }

                    updateListVisibility()
                    successCallBack()
                }
            }
        }
    }
}