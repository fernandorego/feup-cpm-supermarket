package org.feup.group4.supermarket.service

import android.content.Context
import android.graphics.Bitmap
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import org.feup.group4.supermarket.R

class QRService(private val activityResultCaller: ActivityResultCaller) {
    private var callback: ((String?) -> Unit)? = null
    private val qrCodeScannerLauncher: ActivityResultLauncher<ScanOptions> =
        activityResultCaller.registerForActivityResult(
            ScanContract()
        ) { result ->
            callback?.invoke(result.contents)
            this.callback = null
        }

    fun scanQRCode(callback: ((String?) -> Unit)) {
        val scanOptions = ScanOptions()
        scanOptions.setOrientationLocked(false)
        scanOptions.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        try {
            scanOptions.setPrompt((activityResultCaller as Context).resources.getString(R.string.scan_qr_tip))
        } catch (e: Exception) {
            scanOptions.setPrompt((activityResultCaller as Fragment).resources.getString(R.string.scan_qr_tip))
        }

        this.callback = callback
        qrCodeScannerLauncher.launch(scanOptions)
    }

    companion object {
        fun generateQRCode(content: String, width: Int, height: Int): Bitmap {
            val bitMatrix = MultiFormatWriter().encode(
                content, BarcodeFormat.QR_CODE, width, height
            )

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) -0x1000000 else -0x1)
                }
            }
            return bitmap
        }
    }
}
