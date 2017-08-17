package org.mifos.mobilewallet.mifoswallet.injection.component;


import org.mifos.mobilewallet.mifoswallet.injection.module.ActivityModule;

import dagger.Component;
import mifos.org.mobilewallet.core.injection.PerActivity;

@PerActivity
@Component(dependencies = {ApplicationComponent.class},
         modules = {ActivityModule.class})

public interface ActivityComponent {


}
