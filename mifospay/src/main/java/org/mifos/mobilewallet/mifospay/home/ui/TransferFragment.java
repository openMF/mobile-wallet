package org.mifos.mobilewallet.mifospay.home.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.common.ui.MakeTransferFragment;
import org.mifos.mobilewallet.mifospay.home.HomeContract;
import org.mifos.mobilewallet.mifospay.home.presenter.TransferPresenter;
import org.mifos.mobilewallet.mifospay.qr.ui.ReadQrActivity;
import org.mifos.mobilewallet.mifospay.qr.ui.ShowQrActivity;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by naman on 30/8/17.
 */

public class TransferFragment extends BaseFragment implements HomeContract.TransferView {

    private static final int REQUEST_CAMERA = 0;
    private final int SCAN_QR_REQUEST_CODE = 666;
    @Inject
    TransferPresenter mPresenter;
    HomeContract.TransferPresenter mTransferPresenter;
    @BindView(R.id.et_amount)
    EditText etAmount;
    @BindView(R.id.et_vpa)
    EditText etVpa;
    @BindView(R.id.btn_transfer)
    Button btnTransfer;
    @BindView(R.id.tv_client_vpa)
    TextView tvClientVpa;
    @BindView(R.id.btn_show_qr)
    TextView btnShowQr;
    @BindView(R.id.btn_scan_qr)
    TextView btnScanQr;

    private String vpa;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transfer, container,
                false);
        ButterKnife.bind(this, rootView);

        setToolbarTitle("Transfer");
        setSwipeEnabled(false);
        mPresenter.attachView(this);
        hideBackButton();

        mPresenter.fetchVpa();

        return rootView;
    }

    @OnClick(R.id.btn_transfer)
    public void transferClicked() {
        String externalId = etVpa.getText().toString();
        String eamount = etAmount.getText().toString();
        if (eamount.equals("") || externalId.equals("")) {
            Toast.makeText(getActivity(), "Please enter all the fields", Toast.LENGTH_SHORT).show();
        } else {
            double amount = Double.parseDouble(eamount);
            MakeTransferFragment fragment = MakeTransferFragment.newInstance(externalId, amount);
            fragment.show(getChildFragmentManager(), "Make Transfer Fragment");
        }
    }

    @OnClick(R.id.btn_show_qr)
    public void showQrClicked() {
        Intent intent = new Intent(getActivity(), ShowQrActivity.class);
        intent.putExtra(Constants.QR_DATA, vpa);
        startActivity(intent);
    }

    @OnClick(R.id.btn_scan_qr)
    public void scanQrClicked() {

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else {

            // Permission has already been granted
            Intent i = new Intent(getActivity(), ReadQrActivity.class);
            startActivityForResult(i, SCAN_QR_REQUEST_CODE);
        }
    }

    @Override
    public void showVpa(String vpa) {
        this.vpa = vpa;
        tvClientVpa.setText(vpa);
        btnShowQr.setClickable(true);
    }

    @Override
    public void setPresenter(HomeContract.TransferPresenter presenter) {
        this.mTransferPresenter = presenter;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SCAN_QR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String qrData = data.getStringExtra(Constants.QR_DATA);
            etVpa.setText(qrData);
            String externalId = etVpa.getText().toString();
            double amount = Double.parseDouble(etAmount.getText().toString());
            MakeTransferFragment fragment = MakeTransferFragment.newInstance(externalId,
                    amount);
            fragment.show(getChildFragmentManager(), "Make Transfer Fragment");

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // camera-related task you need to do.
                    Intent i = new Intent(getActivity(), ReadQrActivity.class);
                    startActivityForResult(i, SCAN_QR_REQUEST_CODE);

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toaster.show(getView(),
                            "Need camera permission to scan qr code.");
                }
                return;
            }
        }
    }
}