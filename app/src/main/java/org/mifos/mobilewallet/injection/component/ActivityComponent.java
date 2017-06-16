package org.mifos.mobilewallet.injection.component;

import org.mifos.mobilewallet.auth.ui.LandingActivity;
import org.mifos.mobilewallet.injection.PerActivity;
import org.mifos.mobilewallet.injection.module.ActivityModule;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(LandingActivity landingActivity);


}
