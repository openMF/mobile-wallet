package org.mifos.mobilewallet.core.base;

import android.os.Handler;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Executes asynchronous tasks using a {@link ThreadPoolExecutor}.
 * <p>
 * See also {@link Executors} for a list of factory methods to create common
 * {@link java.util.concurrent.ExecutorService}s for different scenarios.
 */
public class UseCaseThreadPoolScheduler implements UseCaseScheduler {

    public static final int POOL_SIZE = 2;
    public static final int MAX_POOL_SIZE = 40;
    public static final int TIMEOUT = 60;
    private final Handler mHandler = new Handler();
    ThreadPoolExecutor mThreadPoolExecutor;

    public UseCaseThreadPoolScheduler() {
        mThreadPoolExecutor = new ThreadPoolExecutor(POOL_SIZE, MAX_POOL_SIZE, TIMEOUT,
                TimeUnit.SECONDS, new ThreadPoolQueue(MAX_POOL_SIZE));
    }

    @Override
    public void execute(Runnable runnable) {
        mThreadPoolExecutor.execute(runnable);
    }


    @Override
    public <V extends UseCase.ResponseValue> void notifyResponse(final V response,
            final UseCase.UseCaseCallback<V>
                    useCaseCallback) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                useCaseCallback.onSuccess(response);
            }
        });
    }

    @Override
    public <V extends UseCase.ResponseValue> void onError(final String message,
            final UseCase.UseCaseCallback<V> useCaseCallback) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                useCaseCallback.onError(message);
            }
        });
    }

}
