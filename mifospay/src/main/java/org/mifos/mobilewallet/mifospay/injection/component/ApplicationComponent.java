package org.mifos.mobilewallet.mifospay.injection.component;

import android.app.Application;
import android.content.Context;

import org.mifos.mobilewallet.mifospay.injection.ApplicationContext;
import org.mifos.mobilewallet.mifospay.injection.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;
import mifos.org.mobilewallet.core.base.UseCaseHandler;
import mifos.org.mobilewallet.core.data.fineract.api.FineractApiManager;
import mifos.org.mobilewallet.core.data.fineract.repository.FineractRepository;
import mifos.org.mobilewallet.core.data.local.LocalRepository;
import mifos.org.mobilewallet.core.data.local.PreferencesHelper;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    @mifos.org.mobilewallet.core.injection.ApplicationContext
    Context libraryContext();

    @ApplicationContext
    Context context();


    Application application();

    UseCaseHandler usecasehandler();
    FineractApiManager fineractApiManager();
    FineractRepository fineractRepository();
    PreferencesHelper prefManager();
    LocalRepository localRepository();



}