package org.mifos.mobilewallet.mifospay.injection.component;


import org.mifos.mobilewallet.mifospay.auth.ui.LoginActivity;
import org.mifos.mobilewallet.mifospay.home.ui.HomeActivity;
import org.mifos.mobilewallet.mifospay.home.ui.HomeFragment;
import org.mifos.mobilewallet.mifospay.home.ui.WalletFragment;
import org.mifos.mobilewallet.mifospay.injection.module.ActivityModule;

import dagger.Component;
import mifos.org.mobilewallet.core.injection.PerActivity;

@PerActivity
@Component(dependencies = {ApplicationComponent.class},
         modules = {ActivityModule.class})

public interface ActivityComponent {

    void inject(LoginActivity loginActivity);

    void inject(HomeActivity homeActivity);

    void inject(HomeFragment homeFragment);

    void inject(WalletFragment walletFragment);


}
