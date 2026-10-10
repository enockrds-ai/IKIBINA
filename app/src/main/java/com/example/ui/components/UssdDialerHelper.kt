package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object UssdDialerHelper {
    const val USSD_CODE = "*182*8*1*412512#"

    /**
     * Attempts to open the phone dialer with the prefilled USSD code "*182*8*1*412512#".
     * Encodes '#' as '%23' so Android dialer opens with the code prefilled.
     * Returns true if dialer was launched, false if blocked or unsupported.
     */
    fun openDialer(context: Context, code: String = USSD_CODE): Boolean {
        return try {
            val encodedCode = Uri.encode(code)
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$encodedCode")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }
}
