package org.mifos.mobilewallet.mifospay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.TextView;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import java.util.Calendar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AboutusFragment extends BaseFragment {

    @BindView(R.id.tv_app_version)
    TextView tvAppVersion;

    @BindView(R.id.tv_copy_right)
    TextView tvCopyRight;

    View rootView;

    public static AboutusFragment newInstance() {
        AboutusFragment fragment = new AboutusFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_aboutus, container, false);
        ButterKnife.bind(this, rootView);
        setToolbarTitle(getString(R.string.about_us));

        tvAppVersion.setText(getString(R.string.app_version, BuildConfig.VERSION_NAME));

        tvCopyRight.setText(getString(R.string.copy_right_mifos,
                String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));

        return rootView;
    }

    @OnClick(R.id.tv_privacy_policy)
    void showPrivacyPolicy() {
        startActivity(new Intent(getActivity(), PrivacyPolicyActivity.class));
    }
}
