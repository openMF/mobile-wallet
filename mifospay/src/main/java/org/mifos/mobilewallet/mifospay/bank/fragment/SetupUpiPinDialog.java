package org.mifos.mobilewallet.mifospay.bank.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.bank.adapters.UpiPinPagerAdapter;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ankur on 16/July/2018
 */

public class SetupUpiPinDialog extends BottomSheetDialogFragment {

    @BindView(R.id.vp_setup_upi_pin)
    ViewPager mVpSetupUpiPin;

    private BottomSheetBehavior mBottomSheetBehavior;
    private String type;
    private Fragment[] mSetupUpiPinFragments;
    private UpiPinPagerAdapter upiPinPagerAdapter;

    public static SetupUpiPinDialog newInstance(String type) {

        Bundle args = new Bundle();
        args.putString(Constants.TYPE, type);
        SetupUpiPinDialog fragment = new SetupUpiPinDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = View.inflate(getContext(), R.layout.dialog_setup_upi_pin2, null);

        mBottomSheetBehavior = BottomSheetBehavior.from((View) rootView.getParent());

        ButterKnife.bind(this, rootView);

        mSetupUpiPinFragments = new Fragment[4];
        mSetupUpiPinFragments[0] = new DebitCardFragment();
        mSetupUpiPinFragments[1] = new OtpFragment();
        mSetupUpiPinFragments[2] = new UpiPinFragment();
        mSetupUpiPinFragments[3] = new UpiPinFragment();
        upiPinPagerAdapter = new UpiPinPagerAdapter(getChildFragmentManager(),
                mSetupUpiPinFragments);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}
