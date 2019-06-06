package org.mifos.mobilewallet.core.base;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by shivansh on 14/July/2019
 */

public final class ThreadPoolQueue extends ArrayBlockingQueue<Runnable> {

    public ThreadPoolQueue(int capacity) {
        super(capacity);
    }

    @Override
    public boolean offer(Runnable e) {
        try {
            put(e);
        } catch (InterruptedException e1) {
            return false;
        }
        return true;
    }

}