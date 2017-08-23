package org.mifos.mobilewallet.injection.module;

import android.app.Application;
import android.content.Context;

import org.mifos.mobilewallet.data.local.PreferencesHelper;
import org.mifos.mobilewallet.data.rbl.api.RblApiManager;
import org.mifos.mobilewallet.injection.ApplicationContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.base.UseCaseThreadPoolScheduler;
import org.mifos.mobilewallet.core.data.fineract.api.FineractApiManager;


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
    RblApiManager provideRblApiManager() {
        return new RblApiManager();
    }

    @Provides
    @Singleton
    PreferencesHelper providePrefManager(@ApplicationContext Context context) {
        return new PreferencesHelper(context);
    }

}