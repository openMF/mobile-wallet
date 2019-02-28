package org.mifos.mobilewallet.mifospay.injection.component;

import android.app.Application;
import android.content.Context;

import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.data.fineract.api.FineractApiManager;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper;
import org.mifos.mobilewallet.mifospay.injection.ApplicationContext;
import org.mifos.mobilewallet.mifospay.injection.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    @ApplicationContext
    Context context();


    Application application();

    UseCaseHandler usecasehandler();

    FineractApiManager fineractApiManager();

    FineractRepository fineractRepository();

    PreferencesHelper prefManager();

    LocalRepository localRepository();
}