package org.feup.group4.supermarket.service

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log
import java.io.IOException

class NFCReaderService : NfcAdapter.ReaderCallback {
    companion object {
        var listener: ((ByteArray) -> Unit)? = null

        fun byteArrayToHex(byteArray: ByteArray): String {
            val stringBuilder = StringBuilder(byteArray.size * 2)
            for (byte in byteArray) {
                stringBuilder.append(String.format("%02x", byte))
            }
            return stringBuilder.toString()
        }

        fun hexStringToByteArray(s: String): ByteArray {
            val data = ByteArray(s.length / 2)
            var k = 0
            while (k < s.length) {
                data[k / 2] =
                    ((Character.digit(s[k], 16) shl 4) + Character.digit(s[k + 1], 16)).toByte()
                k += 2
            }
            return data
        }

        const val SEND_NFC_PREF = "send_nfc"
        const val CARD_AID = "F012233445"
        const val CMD_SEL_AID = "00A40400"
        const val CMD_GET_SECOND = "80010000"
        const val MAX_RES_SIZE = 250
        val SELECT_APDU = hexStringToByteArray(CMD_SEL_AID + String.format("%02X", CARD_AID.length/2) + CARD_AID)
        val OK_SW = hexStringToByteArray("9000")             // "OK" status word (0x9000)
        val UNKNOWN_CMD_SW = hexStringToByteArray("0000")    // "UNKNOWN" command status word (0X0000)
    }

    override fun onTagDiscovered(tag: Tag) {
        println("Descobri tag")
        val isoDep = IsoDep.get(tag) ?: return
        println("Seguiu")
        try {
            isoDep.connect()
            println("Conectou")
            val result = isoDep.transceive(
                hexStringToByteArray(
                    CMD_SEL_AID + String.format(
                        "%02X",
                        CARD_AID.length / 2
                    ) + CARD_AID
                )
            )
            println("Recebeu")
            val rLen = result.size
            val status = byteArrayOf(result[rLen - 2], result[rLen - 1])
            val more = result[0] == 1.toByte()
            if (OK_SW.contentEquals(status)) {
                if (more) {
                    println("More")
                    val second = isoDep.transceive(hexStringToByteArray(CMD_GET_SECOND))
                    val len = second.size
                    if (OK_SW.contentEquals(byteArrayOf(second[len - 2], second[len - 1]))) {
                        listener?.invoke(result.sliceArray(1..rLen - 3) + second.sliceArray(0..len - 3))
                    }
                } else {
                    println("Not more")
                    println(result.sliceArray(1..rLen - 3).toString())
                    listener?.invoke(result.sliceArray(1..rLen - 3))
                }
            }
        } catch (e: IOException) {
            Log.e("CardReader", "Error communicating with card: $e")
        }
    }
}