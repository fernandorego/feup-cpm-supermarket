package org.feup.group4.supermarket.fragments.terminal

import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import org.feup.group4.supermarket.R

import org.feup.group4.supermarket.fragments.PurchaseCompletedDialogFragment
import org.feup.group4.supermarket.model.PaymentResult
import org.feup.group4.supermarket.service.PurchaseService
import org.feup.group4.supermarket.service.QRService
import kotlin.concurrent.thread


class CheckoutFragment : Fragment() {
    @Transient
    private val qrService = QRService(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_terminal_checkout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val qrCodeButton = view.findViewById<Button>(R.id.scan_qr_fab)
        qrCodeButton.setOnClickListener {
            qrService.scanQRCode(::addProduct)
        }

        val nfcButton = view.findViewById<Button>(R.id.nfc_fab)
        nfcButton.setOnClickListener {
            val hasNfc = NfcAdapter.getDefaultAdapter(context) != null
            if (!hasNfc) {
                Toast.makeText(
                    context,
                    resources.getString(R.string.nfc_not_supported),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            ReceiveNFCPurchaseDialogFragment.newInstance(::addProduct).show(
                childFragmentManager, "ReceiveNFCPurchaseDialogFragment"
            )
        }
    }

    private fun addProduct(productString: String?) {
        if (productString == null) {
            Toast.makeText(requireContext(), R.string.scan_qr_error, Toast.LENGTH_LONG).show()
            return
        }
        thread(start = true) {
            PurchaseService(requireContext()).forwardClientPurchase(productString) { statusCode, response ->
                requireActivity().runOnUiThread {
                    if (statusCode == 200) {
                        val paymentInfo = Gson().fromJson(response, PaymentResult::class.java)
                        PurchaseCompletedDialogFragment.newInstance(true, paymentInfo.paid_value)
                            .show(
                                childFragmentManager, "PurchaseCompletedDialogFragment"
                            )
                    } else {
                        PurchaseCompletedDialogFragment.newInstance(false, 0.0).show(
                            childFragmentManager, "PurchaseCompletedDialogFragment"
                        )
                        Toast.makeText(requireContext(), response, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}