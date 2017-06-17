package org.mifos.mobilewallet.injection.component;

import android.app.Application;
import android.content.Context;

import org.mifos.mobilewallet.core.UseCaseHandler;
import org.mifos.mobilewallet.data.api.BaseApiManager;
import org.mifos.mobilewallet.data.local.PreferencesHelper;
import org.mifos.mobilewallet.data.repository.ApiRepository;
import org.mifos.mobilewallet.injection.ApplicationContext;
import org.mifos.mobilewallet.injection.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    @ApplicationContext
    Context context();

    Application application();

    UseCaseHandler usecasehandler();
    BaseApiManager baseApiManager();
    ApiRepository apiRepository();
    PreferencesHelper prefManager();


}