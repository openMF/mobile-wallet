package org.mifos.mobilewallet.home.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.base.BaseFragment;

import butterknife.ButterKnife;

/**
 * Created by naman on 17/6/17.
 */

public class HomeFragment extends BaseFragment {

    View rootView;


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        setToolbarTitle("Home");
        ButterKnife.bind(this, rootView);

        showProgress();
        return rootView;
    }
}
