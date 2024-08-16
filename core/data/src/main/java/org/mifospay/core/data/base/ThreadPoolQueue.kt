/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.base

import java.util.concurrent.ArrayBlockingQueue

class ThreadPoolQueue(capacity: Int) : ArrayBlockingQueue<Runnable?>(capacity) {

    override fun offer(e: Runnable?): Boolean {
        try {
            put(e)
        } catch (e1: InterruptedException) {
            return false
        }
        return true
    }
}
