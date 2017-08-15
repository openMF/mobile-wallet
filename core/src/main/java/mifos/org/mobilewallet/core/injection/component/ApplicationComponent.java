package mifos.org.mobilewallet.core.injection.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import mifos.org.mobilewallet.core.base.UseCaseHandler;
import mifos.org.mobilewallet.core.data.fineract.api.FineractApiManager;
import mifos.org.mobilewallet.core.data.fineract.repository.FineractRepository;
import mifos.org.mobilewallet.core.data.local.LocalRepository;
import mifos.org.mobilewallet.core.data.local.PreferencesHelper;
import mifos.org.mobilewallet.core.injection.ApplicationContext;
import mifos.org.mobilewallet.core.injection.module.ApplicationModule;


@Singleton
@Component(modules = ApplicationModule.class)
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