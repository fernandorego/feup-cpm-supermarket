package org.feup.group4.supermarket.activities.terminal

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity

class ReceiveNFCPurchaseActivity(private val listener: ((ByteArray) -> Unit)) :
    AppCompatActivity() {
    override fun onResume() {
        super.onResume()
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            val ndefMessage =
                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)!![0] as NdefMessage
            val payload = ndefMessage.records[0].payload
            listener(payload)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}