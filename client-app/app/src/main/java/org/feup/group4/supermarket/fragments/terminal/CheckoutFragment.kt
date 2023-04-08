package org.feup.group4.supermarket.fragments.terminal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import org.feup.group4.supermarket.R

import org.feup.group4.supermarket.fragments.PurchaseCompletedDialogFragment
import org.feup.group4.supermarket.service.QRService


class CheckoutFragment : Fragment() {
    private val qrService = QRService(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_terminal_checkout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val qrCodeButton = view.findViewById<Button>(R.id.scan_qr_fab)
        qrCodeButton.setOnClickListener {
            qrService.scanQRCode { _ ->
                // TODO: Actually do something with the QR code
                PurchaseCompletedDialogFragment(true).show(
                    childFragmentManager, "PurchaseCompletedDialogFragment"
                )
            }
        }

        val nfcButton = view.findViewById<Button>(R.id.nfc_fab)
        nfcButton.setOnClickListener {
            // TODO: NFC
            println("TODO: NFC")
        }
    }
}