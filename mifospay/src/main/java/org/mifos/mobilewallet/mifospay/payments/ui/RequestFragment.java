package org.mifos.mobilewallet.mifospay.payments.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract;
import org.mifos.mobilewallet.mifospay.payments.presenter.TransferPresenter;
import org.mifos.mobilewallet.mifospay.qr.ui.ShowQrActivity;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class RequestFragment extends BaseFragment implements BaseHomeContract.TransferView {

    @Inject
    TransferPresenter mPresenter;

    BaseHomeContract.TransferPresenter mTransferPresenter;

    @BindView(R.id.tv_client_mobile)
    TextView mTvClientMobile;

    @BindView(R.id.tv_client_vpa)
    TextView tvClientVpa;

    @BindView(R.id.btn_show_qr)
    TextView btnShowQr;

    private String vpa;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_request, container, false);
        ButterKnife.bind(this, root);
        mPresenter.attachView(this);
        mPresenter.fetchVpa();
        mPresenter.fetchMobile();
        return root;
    }

    @Override
    public void setPresenter(BaseHomeContract.TransferPresenter presenter) {
        this.mTransferPresenter = presenter;
    }

    @OnClick(R.id.btn_show_qr)
    public void showQrClicked() {
        Intent intent = new Intent(getActivity(), ShowQrActivity.class);
        intent.putExtra(Constants.QR_DATA, vpa);
        startActivity(intent);
    }

    @Override
    public void showVpa(String vpa) {
        this.vpa = vpa;
        tvClientVpa.setText(vpa);
        btnShowQr.setClickable(true);
    }


    @Override
    public void showMobile(String mobileNo) {
        PhoneNumberUtil phoneNumberUtil =
                PhoneNumberUtil.createInstance(mTvClientMobile.getContext());
        try {
            Phonenumber.PhoneNumber phoneNumber =
                    phoneNumberUtil.parse(mobileNo, Locale.getDefault().getCountry());
            mTvClientMobile.setText(phoneNumberUtil.format(phoneNumber,
                    PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL));
        } catch (NumberParseException e) {
            mTvClientMobile.setText(mobileNo); // If mobile number is not parsed properly
        }
    }

    @Override
    public void showClientDetails(String externalId, double amount) {

    }

    @Override
    public void showToast(String message) {
        Toaster.showToast(getContext(), message);
    }

    @Override
    public void showSnackbar(String message) {
        Toaster.show(getView(), message);
    }
}
