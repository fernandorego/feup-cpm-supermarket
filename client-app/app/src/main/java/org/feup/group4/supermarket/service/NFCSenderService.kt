package org.feup.group4.supermarket.service

import android.content.Intent
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.preference.PreferenceManager

class NFCSenderService : HostApduService() {
    private var byteArray: ByteArray? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent!!.extras != null) {
            byteArray = intent.getByteArrayExtra("content")
        }
        return START_NOT_STICKY
    }

    override fun processCommandApdu(command: ByteArray, extra: Bundle?): ByteArray {
        if (byteArray == null || !PreferenceManager.getDefaultSharedPreferences(applicationContext)
                .getBoolean(NFCReaderService.SEND_NFC_PREF, false)
        ) {
            println("Nao deveria receber nada agora")
            return NFCReaderService.UNKNOWN_CMD_SW
        }

        if (NFCReaderService.SELECT_APDU.contentEquals(command)) {
            if (byteArray!!.size >= NFCReaderService.MAX_RES_SIZE) {
                println("Mt grande")
                return NFCReaderService.UNKNOWN_CMD_SW
            }
            println("Sent nfc stuff!")
            return byteArray!!
        }

        println("Tensei nfc")
        return NFCReaderService.UNKNOWN_CMD_SW
    }

    override fun onDeactivated(p0: Int) {
        // TODO: Implement this
        println("Sou um serviço nfc send desativado")
    }
}