package org.mifos.mobilewallet.mifospay.injection.module;

import android.app.Application;
import android.content.Context;

import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper;
import org.mifos.mobilewallet.mifospay.injection.ApplicationContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import mifos.org.mobilewallet.core.base.UseCaseHandler;
import mifos.org.mobilewallet.core.base.UseCaseThreadPoolScheduler;
import mifos.org.mobilewallet.core.data.fineract.api.FineractApiManager;

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

    @Provides
    @Singleton
    FineractApiManager provideFineractApiManager() {
        return new FineractApiManager();
    }

    @Provides
    @Singleton
    PreferencesHelper providePrefManager(@ApplicationContext Context context) {
        return new PreferencesHelper(context);
    }


}