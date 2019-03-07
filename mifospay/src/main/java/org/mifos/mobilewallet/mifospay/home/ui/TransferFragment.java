package org.mifos.mobilewallet.mifospay.home.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

/**
 * Created by naman on 30/8/17.
 */

public class TransferFragment extends BaseFragment implements HomeContract.TransferView {

    public static final int REQUEST_SHOW_DETAILS = 3;
    private static final int REQUEST_CAMERA = 0;
    private static final int SCAN_QR_REQUEST_CODE = 666;
    private static final int PICK_CONTACT = 1;
    private static final int REQUEST_READ_CONTACTS = 2;
    private static final String AMOUNT_DECIMAL_SEPARATOR = ".";

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
    @BindView(R.id.btn_vpa)
    Button mBtnVpa;
    @BindView(R.id.btn_mobile)
    Button mBtnMobile;
    @BindView(R.id.et_mobile_number)
    EditText mEtMobileNumber;
    @BindView(R.id.btn_search_contact)
    TextView mBtnSearchContact;
    @BindView(R.id.rl_mobile)
    RelativeLayout mRlMobile;
    @BindView(R.id.tv_client_mobile)
    TextView mTvClientMobile;
    @BindView(R.id.til_vpa)
    TextInputLayout mTilVpa;

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

        setToolbarTitle(Constants.TRANSFER);
        setSwipeEnabled(false);
        mPresenter.attachView(this);
        hideBackButton();

        mPresenter.fetchVpa();
        mPresenter.fetchMobile();

        mEtMobileNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            /**
             * An overridden method used to allow the user to enter amount upto 2 decimal places
             * @param s : CharSequence entered by user
             * @param start : Starting index of CharSequence
             * @param before : Length of old text
             * @param count : To count number of characters
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(AMOUNT_DECIMAL_SEPARATOR) &&
                        s.toString().length() -
                                s.toString().indexOf(AMOUNT_DECIMAL_SEPARATOR) > 3) {
                    etAmount.setText(s.toString().substring(0, s.length() - 1));
                    etAmount.setSelection(etAmount.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return rootView;
    }

    @OnClick(R.id.btn_vpa)
    public void onVPASelected() {
        mBtnVpa.setFocusable(true);
        mBtnVpa.setFocusableInTouchMode(true);

        mBtnVpa.setBackgroundResource(R.drawable.button_round_primary);
        mBtnMobile.setBackgroundResource(R.drawable.button_round_stroke);
        mBtnVpa.setTextColor(getResources().getColor(android.R.color.white));
        mBtnMobile.setTextColor(getResources().getColor(android.R.color.black));

        mRlMobile.setVisibility(View.GONE);
        mTilVpa.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_mobile)
    public void onMobileSelected() {
        mBtnMobile.setFocusable(true);
        mBtnMobile.setFocusableInTouchMode(true);

        mBtnMobile.setBackgroundResource(R.drawable.button_round_primary);
        mBtnVpa.setBackgroundResource(R.drawable.button_round_stroke);
        mBtnMobile.setTextColor(getResources().getColor(android.R.color.white));
        mBtnVpa.setTextColor(getResources().getColor(android.R.color.black));

        mTilVpa.setVisibility(View.GONE);
        mRlMobile.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_transfer)
    public void transferClicked() {
        String externalId = etVpa.getText().toString().trim();
        String eamount = etAmount.getText().toString().trim();
        String mobileNumber = mEtMobileNumber.getText().toString().trim().replaceAll("\\s+", "");
        if (eamount.equals("") || (externalId.equals("") && mobileNumber.equals(""))) {
            if (eamount.equals("")) {
                etAmount.requestFocus();
                etAmount.setError("Please enter amount");
            }

            if (externalId.equals("")) {
                etVpa.requestFocus();
                etVpa.setError("Please enter Virtual Payment Address");
            }
        } else {
            double amount = Double.parseDouble(eamount);
            if (amount <= 0) {
                showSnackbar(Constants.PLEASE_ENTER_VALID_AMOUNT);
                return;
            }
            showSwipeProgress();
            mTransferPresenter.checkBalanceAvailability(externalId, amount);
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

            if (etAmount.getText().toString().trim().equals("")) {
                etAmount.requestFocus();
                etAmount.setError("Please enter amount");
            } else {
                // Permission has already been granted
                Intent i = new Intent(getActivity(), ReadQrActivity.class);
                startActivityForResult(i, SCAN_QR_REQUEST_CODE);
            }
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

    @OnClick(R.id.btn_search_contact)
    public void searchContactClicked() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        } else {

            // Permission has already been granted
            Intent intent = new Intent(Intent.ACTION_PICK,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(intent, PICK_CONTACT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SCAN_QR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String qrData = data.getStringExtra(Constants.QR_DATA);
            etVpa.setText(qrData);
            String externalId = etVpa.getText().toString().trim();
            double amount = Double.parseDouble(etAmount.getText().toString().trim());
            MakeTransferFragment fragment = MakeTransferFragment.newInstance(externalId,
                    amount);
            fragment.show(getChildFragmentManager(),
                    Constants.MAKE_TRANSFER_FRAGMENT);

        } else if (requestCode == PICK_CONTACT && resultCode == Activity.RESULT_OK) {
            Cursor cursor = null;
            try {
                String phoneNo = null;
                String name = null;
                // getData() method will have the Content Uri of the selected contact
                Uri uri = data.getData();
                //Query the content uri
                cursor = getContext().getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();
                // column index of the phone number
                int phoneIndex = cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER);
                // column index of the contact name
                int nameIndex = cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                phoneNo = cursor.getString(phoneIndex);
                name = cursor.getString(nameIndex);

                mEtMobileNumber.setText(phoneNo);

            } catch (Exception e) {
                showToast(Constants.ERROR_CHOOSING_CONTACT);
            }
        } else if (requestCode == REQUEST_SHOW_DETAILS && resultCode == Activity.RESULT_CANCELED) {
            showSnackbar(Constants.ERROR_FINDING_VPA);
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
                    Toaster.show(getView(), Constants.NEED_CAMERA_PERMISSION_TO_SCAN_QR_CODE);
                }
                return;
            }
            case REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT);

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toaster.show(getView(), Constants.NEED_READ_CONTACTS_PERMISSION);
                }
            }
        }
    }

    @Override
    public void showToast(String message) {
        Toaster.showToast(getContext(), message);
    }

    @Override
    public void showSnackbar(String message) {
        Toaster.show(getView(), message);
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
        MakeTransferFragment fragment = MakeTransferFragment.newInstance(externalId, amount);
        fragment.setTargetFragment(this, REQUEST_SHOW_DETAILS);
        if (getParentFragment() != null) {
            fragment.show(getParentFragment().getChildFragmentManager(),
                    Constants.MAKE_TRANSFER_FRAGMENT);
        }
    }
}