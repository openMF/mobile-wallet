package org.mifos.mobilewallet.mifospay.injection.module;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import mifos.org.mobilewallet.core.base.UseCaseHandler;
import mifos.org.mobilewallet.core.base.UseCaseThreadPoolScheduler;
import mifos.org.mobilewallet.core.data.fineract.api.FineractApiManager;
import mifos.org.mobilewallet.core.injection.ApplicationContext;


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
    @mifos.org.mobilewallet.core.injection.ApplicationContext
    Context provideLibraryContext() {
        return application;
    }

    @Provides
    @org.mifos.mobilewallet.mifospay.injection.ApplicationContext
    Context provideContext() {
        return application;
    }

    @Provides
    UseCaseHandler provideUsecaseHandler() {
        return new UseCaseHandler(new UseCaseThreadPoolScheduler());
    }

    @Provides
    @Singleton
    FineractApiManager provideFineractApiManager() {
        return new FineractApiManager();
    }


}