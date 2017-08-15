package mifos.org.mobilewallet.core.injection.component;


import dagger.Component;
import mifos.org.mobilewallet.core.injection.PerActivity;
import mifos.org.mobilewallet.core.injection.module.ActivityModule;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

}
