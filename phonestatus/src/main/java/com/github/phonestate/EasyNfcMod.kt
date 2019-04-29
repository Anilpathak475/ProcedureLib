/*
 * Copyright (C) 2016 Nishant Srivastava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
