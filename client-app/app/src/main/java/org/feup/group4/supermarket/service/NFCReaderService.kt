package org.feup.group4.supermarket.service

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log
import java.io.IOException

class NFCReaderService(private val listener: ((ByteArray) -> Unit)) : NfcAdapter.ReaderCallback {
    companion object {
        private fun hexStringToByteArray(s: String): ByteArray {
            val data = ByteArray(s.length / 2)
            var k = 0
            while (k < s.length) {
                data[k / 2] =
                    ((Character.digit(s[k], 16) shl 4) + Character.digit(s[k + 1], 16)).toByte()
                k += 2
            }
            return data
        }

        const val NFC_PREF_SEND = "send_nfc"
        private const val NFC_CARD_AID = "F012233445"
        private const val NFC_SEL_AID = "00A40400"
        const val NFC_MAX_RES_SIZE = 250
        val NFC_CMD_SELECT_APDU = hexStringToByteArray(
            NFC_SEL_AID + String.format(
                "%02X",
                NFC_CARD_AID.length / 2
            ) + NFC_CARD_AID
        )
        val NFC_CMD_OK_FINISHED = hexStringToByteArray("9000")
        val NFC_CMD_OK_MORE = hexStringToByteArray("6100")
        val NFC_CMD_ERROR = hexStringToByteArray("6F00")
        val NFC_CMD_UNKNOWN = hexStringToByteArray("0000")
    }

    override fun onTagDiscovered(tag: Tag) {
        val isoDep = IsoDep.get(tag) ?: return
        try {
            isoDep.connect()

            var content = byteArrayOf()
            while (true) {
                val meetResult = isoDep.transceive(
                    NFC_CMD_SELECT_APDU
                )
                val meetResultLength = meetResult.size
                if (meetResultLength < 2) {
                    Log.w("CardReader", "Unknown response: $meetResult")
                    break
                }
                val status =
                    byteArrayOf(meetResult[meetResultLength - 2], meetResult[meetResultLength - 1])
                if (NFC_CMD_OK_FINISHED.contentEquals(status)) {
                    content += meetResult.sliceArray(0..meetResultLength - 3)
                    listener.invoke(content)
                    break
                } else if (NFC_CMD_OK_MORE.contentEquals(status)) {
                    content += meetResult.sliceArray(0..meetResultLength - 3)
                } else if (NFC_CMD_ERROR.contentEquals(status)) {
                    content = byteArrayOf()
                } else {
                    Log.w("CardReader", "Unknown status: $status")
                    break
                }
            }
        } catch (e: IOException) {
            Log.e("CardReader", "Error communicating with card: $e")
        }
    }
}