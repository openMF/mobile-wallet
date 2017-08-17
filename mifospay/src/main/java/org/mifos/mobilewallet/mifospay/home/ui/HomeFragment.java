package org.mifos.mobilewallet.mifospay.home.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.mifos.mobilewallet.mifospay.base.BaseFragment;

/**
 * Created by naman on 17/8/17.
 */

public class HomeFragment extends BaseFragment {

    public static HomeFragment newInstance() {

        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
