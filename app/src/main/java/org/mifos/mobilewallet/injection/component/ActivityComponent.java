package org.mifos.mobilewallet.injection.component;

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

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(LandingActivity landingActivity);

    void inject(LoginActivity loginActivity);

    void inject(SignupActivity signupActivity);

    void inject(AddAccountActivity addAccountActivity);

    void inject(BusinessDetailsActivity businessDetailsActivity);

    void inject(BankAccountActivity bankAccountActivity);

    void inject(SetupCompleteActivity setupCompleteActivity);

    void inject(HomeActivity homeActivity);

    void inject(HomeFragment homeFragment);


}
