package org.feup.group4.supermarket.fragments.terminal

import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.tech.Ndef
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.service.NFCReaderService

class ReceiveNFCPurchaseDialogFragment : AppCompatDialogFragment() {
    class ReceiveNFCPurchaseDialog(context: Context) : AppCompatDialog(context) {
        private val nfcAdapter: NfcAdapter? by lazy { NfcAdapter.getDefaultAdapter(context) }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setCancelable(false)
            setTitle(context.resources.getString(R.string.scanning_nfc_title))
            setContentView(R.layout.dialog_nfc_progress)

            val progressText = findViewById<TextView>(R.id.nfc_progress_text)
            progressText?.text = context.resources.getString(R.string.scanning_nfc_message_receiver)

            val cancelButton = findViewById<TextView>(R.id.cancel_button)
            cancelButton?.setOnClickListener {
                dismiss()
            }

            NFCReaderService.listener = {
                println("sou lindo")
            }

            val nfcReaderService = NFCReaderService()
            NFCReaderService.listener = { content ->
                print("recebi cenas")
                println(content.toString(Charsets.UTF_8))
            }

            nfcAdapter!!.enableReaderMode(
                ownerActivity,
                nfcReaderService,
                NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                null
            )

            setOnDismissListener { nfcAdapter!!.disableReaderMode(ownerActivity) }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): AppCompatDialog {
        super.onCreateDialog(savedInstanceState)
        return ReceiveNFCPurchaseDialog(activity as Context)
    }
}