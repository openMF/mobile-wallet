package org.mifospay.core.data.base

import java.util.concurrent.ArrayBlockingQueue

/**
 * Created by shivansh on 14/July/2019
 */
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
