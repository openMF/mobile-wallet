package org.mifos.mobilewallet.mifospay.injection.component;


import org.mifos.mobilewallet.mifospay.auth.ui.LoginActivity;
import org.mifos.mobilewallet.mifospay.common.ui.SearchActivity;
import org.mifos.mobilewallet.mifospay.home.ui.HomeActivity;
import org.mifos.mobilewallet.mifospay.home.ui.HomeFragment;
import org.mifos.mobilewallet.mifospay.home.ui.WalletFragment;
import org.mifos.mobilewallet.mifospay.injection.PerActivity;
import org.mifos.mobilewallet.mifospay.injection.module.ActivityModule;
import org.mifos.mobilewallet.mifospay.wallet.ui.WalletDetailActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = {ApplicationComponent.class},
         modules = {ActivityModule.class})

public interface ActivityComponent {

    void inject(LoginActivity loginActivity);

    void inject(HomeActivity homeActivity);

    void inject(HomeFragment homeFragment);

    void inject(WalletFragment walletFragment);

    void inject(WalletDetailActivity walletDetailActivity);

    void inject(SearchActivity searchActivity);

}
