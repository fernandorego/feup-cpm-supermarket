package org.feup.group4.supermarket.activities.terminal

import android.app.Activity
import android.nfc.NfcAdapter
import android.nfc.tech.Ndef
import android.widget.Toast

class ReceiveNFCPurchaseActivity :
    Activity() {
    private val nfcAdapter: NfcAdapter? by lazy { NfcAdapter.getDefaultAdapter(this) }

    override fun onResume() {
        super.onResume()
        Toast.makeText(this, "NFC enabled: ${nfcAdapter != null}", Toast.LENGTH_SHORT).show()
        nfcAdapter?.enableReaderMode(this, { tag ->
            val ndef = Ndef.get(tag)
            ndef.connect()
            val message = ndef.ndefMessage
            ndef.close()
            Toast.makeText(this, String(message.records[0].payload), Toast.LENGTH_SHORT).show()
        }, NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
    }
}