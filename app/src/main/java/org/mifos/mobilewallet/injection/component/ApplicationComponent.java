package org.mifos.mobilewallet.injection.component;

import android.app.Application;
import android.content.Context;

import org.mifos.mobilewallet.base.UseCaseHandler;
import org.mifos.mobilewallet.data.fineract.api.FineractApiManager;
import org.mifos.mobilewallet.data.local.LocalRepository;
import org.mifos.mobilewallet.data.local.PreferencesHelper;
import org.mifos.mobilewallet.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.data.pixiepay.api.PixiePayApiManager;
import org.mifos.mobilewallet.data.pixiepay.repository.PixiePayRepository;
import org.mifos.mobilewallet.data.rbl.api.RblApiManager;
import org.mifos.mobilewallet.data.rbl.repository.RblRepository;
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
    FineractApiManager fineractApiManager();
    RblApiManager rblApiManager();
    PixiePayApiManager pixiePayApiManager();
    FineractRepository fineractRepository();
    RblRepository rblRepository();
    PixiePayRepository pixiePayRepository();
    PreferencesHelper prefManager();
    LocalRepository localRepository();


}