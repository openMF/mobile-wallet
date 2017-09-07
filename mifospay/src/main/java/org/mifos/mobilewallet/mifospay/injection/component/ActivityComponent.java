package org.mifos.mobilewallet.mifospay.injection.component;


import org.mifos.mobilewallet.mifospay.auth.ui.LoginActivity;
import org.mifos.mobilewallet.mifospay.common.ui.MakeTransferFragment;
import org.mifos.mobilewallet.mifospay.common.ui.SearchActivity;
import org.mifos.mobilewallet.mifospay.home.ui.HomeActivity;
import org.mifos.mobilewallet.mifospay.home.ui.HomeFragment;
import org.mifos.mobilewallet.mifospay.home.ui.ProfileFragment;
import org.mifos.mobilewallet.mifospay.home.ui.TransferFragment;
import org.mifos.mobilewallet.mifospay.home.ui.WalletFragment;
import org.mifos.mobilewallet.mifospay.injection.PerActivity;
import org.mifos.mobilewallet.mifospay.injection.module.ActivityModule;
import org.mifos.mobilewallet.mifospay.qr.ui.ReadQrActivity;
import org.mifos.mobilewallet.mifospay.qr.ui.ShowQrActivity;
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

    void inject(ShowQrActivity showQrActivity);

    void inject(ReadQrActivity readQrActivity);

    void inject(TransferFragment transferFragment);

    void inject(ProfileFragment profileFragment);

    void inject(MakeTransferFragment transferFragment);

}
