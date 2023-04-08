package org.feup.group4.supermarket.fragments.terminal

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


class CheckoutFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_terminal_checkout, container, false)
}