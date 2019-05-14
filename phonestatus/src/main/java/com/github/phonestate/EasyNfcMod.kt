
package com.github.phonestate

import android.content.Context
import android.nfc.NfcAdapter

/**
 * EasyNfc Mod Class
 */
class EasyNfcMod
/**
 * Instantiates a new Easy nfc mod.
 *
 * @param context the context
 */
    (context: Context) {

    private val nfcAdapter: NfcAdapter?

    /**
     * Is nfc enabled boolean.
     *
     * @return the boolean
     */
    val isNfcEnabled: Boolean
        get() = nfcAdapter != null && this.nfcAdapter.isEnabled

    /**
     * Is nfc present boolean.
     *
     * @return the boolean
     */
    val isNfcPresent: Boolean
        get() = nfcAdapter != null

    init {
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(context)
    }
}
