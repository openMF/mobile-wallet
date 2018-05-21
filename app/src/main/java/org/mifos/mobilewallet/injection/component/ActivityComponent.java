package org.mifos.mobilewallet.injection.component;

import org.mifos.mobilewallet.account.ui.AccountsFragment;
import org.mifos.mobilewallet.auth.ui.AddAccountActivity;
import org.mifos.mobilewallet.auth.ui.BankAccountActivity;
import org.mifos.mobilewallet.auth.ui.BusinessDetailsActivity;
import org.mifos.mobilewallet.auth.ui.LandingActivity;
import org.mifos.mobilewallet.auth.ui.LoginActivity;
import org.mifos.mobilewallet.auth.ui.SetupCompleteActivity;
import org.mifos.mobilewallet.auth.ui.SignupActivity;
import org.mifos.mobilewallet.home.ui.HomeActivity;
import org.mifos.mobilewallet.home.ui.HomeFragment;
import org.mifos.mobilewallet.injection.PerActivity;
import org.mifos.mobilewallet.injection.module.ActivityModule;
import org.mifos.mobilewallet.invoice.ui.AadharPaymentFragment;
import org.mifos.mobilewallet.invoice.ui.CardPaymentFragment;
import org.mifos.mobilewallet.invoice.ui.ExternalPaymentFragment;
import org.mifos.mobilewallet.invoice.ui.InvoiceFragment;
import org.mifos.mobilewallet.invoice.ui.RecentInvoicesFragment;
import org.mifos.mobilewallet.invoice.ui.UpiPaymentFragment;
import org.mifos.mobilewallet.qr.ui.ShowQrActivity;
import org.mifos.mobilewallet.savedcards.ui.CardsFragment;
import org.mifos.mobilewallet.user.ui.UserDetailsActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = {ApplicationComponent.class},
        modules = {ActivityModule.class})

public interface ActivityComponent {

    void inject(LandingActivity landingActivity);

    void inject(LoginActivity loginActivity);

    void inject(SignupActivity signupActivity);

    void inject(AddAccountActivity addAccountActivity);

    void inject(BusinessDetailsActivity businessDetailsActivity);

    void inject(BankAccountActivity bankAccountActivity);

    void inject(SetupCompleteActivity setupCompleteActivity);

    void inject(HomeActivity homeActivity);

    void inject(UserDetailsActivity userDetailsActivity);

    void inject(ShowQrActivity showQrActivity);

    void inject(HomeFragment homeFragment);

    void inject(InvoiceFragment invoiceFragment);

    void inject(RecentInvoicesFragment recentInvoicesFragment);

    void inject(AccountsFragment accountsFragment);

    void inject(UpiPaymentFragment upiPaymentFragment);

    void inject(CardPaymentFragment cardPaymentFragment);

    void inject(AadharPaymentFragment aadharPaymentFragment);

    void inject(ExternalPaymentFragment externalPaymentFragment);

    void inject(CardsFragment cardsFragment);

}
