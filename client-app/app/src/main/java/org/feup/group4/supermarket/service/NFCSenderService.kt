package org.feup.group4.supermarket.service

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.preference.PreferenceManager
import kotlin.math.ceil

class NFCSenderService : HostApduService() {
    companion object {
        private var byteArray: ByteArray? = null
        private var index = 0
        private var numberOfSlices = 0
        fun setByteArray(byteArray: ByteArray) {
            NFCSenderService.byteArray = byteArray
            index = 0
            numberOfSlices =
                ceil(byteArray.size.toDouble() / NFCReaderService.NFC_MAX_RES_SIZE).toInt()
        }
    }

    override fun processCommandApdu(command: ByteArray, extra: Bundle?): ByteArray {
        if (index == numberOfSlices) {
            index = 0
        }

        if (byteArray == null || !PreferenceManager.getDefaultSharedPreferences(
                applicationContext
            ).getBoolean(NFCReaderService.NFC_PREF_SEND, false)
        ) {
            return NFCReaderService.NFC_CMD_UNKNOWN
        }

        if (NFCReaderService.NFC_CMD_SELECT_APDU.contentEquals(command)) {
            val slice = byteArray!!.sliceArray(
                index * NFCReaderService.NFC_MAX_RES_SIZE until kotlin.math.min(
                    index * NFCReaderService.NFC_MAX_RES_SIZE + NFCReaderService.NFC_MAX_RES_SIZE,
                    byteArray!!.size
                )
            )
            index += 1

            return if (index == numberOfSlices) {
                slice + NFCReaderService.NFC_CMD_OK_FINISHED
            } else {
                slice + NFCReaderService.NFC_CMD_OK_MORE
            }
        }

        return NFCReaderService.NFC_CMD_ERROR
    }

    override fun onDeactivated(p0: Int) {}
}