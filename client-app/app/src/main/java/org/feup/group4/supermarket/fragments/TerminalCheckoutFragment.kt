package org.feup.group4.supermarket.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.model.Product
import org.feup.group4.supermarket.service.ProductService
import java.util.*
import kotlin.concurrent.thread


class TerminalCheckoutFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_terminal_checkout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val getProductsButton = view.findViewById<Button>(R.id.get_products_button)
        getProductsButton.setOnClickListener {
            thread(start = true) {
                ProductService(requireContext()).getProducts { products ->
                    println(products)
                }
            }
        }

        val getProductButton = view.findViewById<Button>(R.id.get_product_button)
        getProductButton.setOnClickListener {
            thread(start = true) {
                ProductService(requireContext()).getDecryptedProduct(UUID.fromString("bdc0f964-68df-419f-8f21-690fb3b28748")) { product ->
                    println(product)
                }
            }
        }

        val addProduct = view.findViewById<Button>(R.id.add_Product_button)
        addProduct.setOnClickListener {
            thread(start = true) {
                ProductService(requireContext()).createUpdateProduct(
                    Product(
                        "Leite Agros",
                        1,
                        99
                    )
                ) {
                    println("Product sent")
                }
            }
        }
    }
}