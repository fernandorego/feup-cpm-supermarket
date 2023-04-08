package org.feup.group4.supermarket.service

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import org.feup.group4.supermarket.R

class QRService(private val activity: ComponentActivity) {
    private var callback: ((String?) -> Unit)? = null
    private val qrCodeScannerLauncher: ActivityResultLauncher<ScanOptions> =
        activity.registerForActivityResult(
            ScanContract()
        ) { result ->
            callback?.invoke(result.contents)
            this.callback = null
        }

    fun scanQRCode(callback: ((String?) -> Unit)) {
        val scanOptions = ScanOptions()
        scanOptions.setOrientationLocked(false)
        scanOptions.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        scanOptions.setPrompt(activity.resources.getString(R.string.scan_qr_tip))

        this.callback = callback
        qrCodeScannerLauncher.launch(scanOptions)
    }
}