package org.mifos.mobilewallet.injection.module;

import android.app.Application;
import android.content.Context;

import org.mifos.mobilewallet.core.UseCaseHandler;
import org.mifos.mobilewallet.core.UseCaseThreadPoolScheduler;
import org.mifos.mobilewallet.injection.ApplicationContext;

import dagger.Module;
import dagger.Provides;


@Module
public class ApplicationModule {

    private Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    Application provideApplication() {
        return application;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return application;
    }

    @Provides
    UseCaseHandler provideUsecaseHandler() {
        return new UseCaseHandler(new UseCaseThreadPoolScheduler());
    }
}