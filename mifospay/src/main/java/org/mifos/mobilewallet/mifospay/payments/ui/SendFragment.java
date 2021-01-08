package org.mifos.mobilewallet.mifospay.payments.ui;

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
import android.support.design.chip.Chip;
import android.support.design.widget.TextInputLayout;
import android.support.transition.TransitionManager;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.common.ui.MakeTransferFragment;
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract;
import org.mifos.mobilewallet.mifospay.payments.presenter.TransferPresenter;
import org.mifos.mobilewallet.mifospay.qr.ui.ReadQrActivity;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.Toaster;
import org.mifos.mobilewallet.mifospay.utils.Utils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by naman on 30/8/17.
 */

public class SendFragment extends BaseFragment implements BaseHomeContract.TransferView {

    public static final int REQUEST_SHOW_DETAILS = 3;
    private static final int REQUEST_CAMERA = 0;
    private static final int SCAN_QR_REQUEST_CODE = 666;
    private static final int PICK_CONTACT = 1;
    private static final int REQUEST_READ_CONTACTS = 2;

    @Inject
    TransferPresenter mPresenter;
    BaseHomeContract.TransferPresenter mTransferPresenter;
    @BindView(R.id.rl_send_container)
    ViewGroup sendContainer;
    @BindView(R.id.et_amount)
    EditText etAmount;
    @BindView(R.id.et_vpa)
    EditText etVpa;
    @BindView(R.id.btn_submit)
    Button btnTransfer;
    @BindView(R.id.btn_scan_qr)
    TextView btnScanQr;
    @BindView(R.id.btn_vpa)
    Chip mBtnVpa;
    @BindView(R.id.btn_mobile)
    Chip mBtnMobile;
    @BindView(R.id.et_mobile_number)
    EditText mEtMobileNumber;
    @BindView(R.id.btn_search_contact)
    TextView mBtnSearchContact;
    @BindView(R.id.rl_mobile)
    RelativeLayout mRlMobile;
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
        View rootView = inflater.inflate(R.layout.fragment_send, container,
                false);
        ButterKnife.bind(this, rootView);
        setSwipeEnabled(false);
        mPresenter.attachView(this);
        mEtMobileNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        mBtnVpa.setSelected(true);
        return rootView;
    }

    @OnClick(R.id.btn_vpa)
    public void onVPASelected() {
        TransitionManager.beginDelayedTransition(sendContainer);
        mBtnVpa.setSelected(true);
        mBtnVpa.setFocusable(true);
        mBtnVpa.setChipBackgroundColorResource(R.color.clickedblue);
        mBtnMobile.setSelected(false);
        mBtnMobile.setChipBackgroundColorResource(R.color.changedBackgroundColour);
        btnScanQr.setVisibility(View.VISIBLE);
        mRlMobile.setVisibility(View.GONE);
        mTilVpa.setVisibility(View.VISIBLE);
        Utils.hideSoftKeyboard(getActivity());
    }

    @OnClick(R.id.btn_mobile)
    public void onMobileSelected() {
        TransitionManager.beginDelayedTransition(sendContainer);
        mBtnMobile.setSelected(true);
        mBtnMobile.setFocusable(true);
        mBtnMobile.setChipBackgroundColorResource(R.color.clickedblue);
        mBtnVpa.setSelected(false);
        mBtnVpa.setChipBackgroundColorResource(R.color.changedBackgroundColour);
        mTilVpa.setVisibility(View.GONE);
        btnScanQr.setVisibility(View.GONE);
        mRlMobile.setVisibility(View.VISIBLE);
        Utils.hideSoftKeyboard(getActivity());
    }

    @OnClick(R.id.btn_submit)
    public void transferClicked() {
        String externalId = etVpa.getText().toString().trim();
        String eamount = etAmount.getText().toString().trim();
        String mobileNumber = mEtMobileNumber.getText()
                .toString().trim().replaceAll("\\s+", "");
        if (eamount.equals("") || (mBtnVpa.isSelected() && externalId.equals("")) ||
                (mBtnMobile.isSelected() && mobileNumber.equals(""))) {
            Toast.makeText(getActivity(),
                    Constants.PLEASE_ENTER_ALL_THE_FIELDS, Toast.LENGTH_SHORT).show();
        } else {
            double amount = Double.parseDouble(eamount);
            if (amount <= 0) {
                showSnackbar(Constants.PLEASE_ENTER_VALID_AMOUNT);
                return;
            }
            if (!mTransferPresenter.checkSelfTransfer(externalId)) {
                mTransferPresenter.checkBalanceAvailability(externalId, amount);
            } else {
                showSnackbar(Constants.SELF_ACCOUNT_ERROR);
            }
        }
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
    }

    @Override
    public void setPresenter(BaseHomeContract.TransferPresenter presenter) {
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
            final String[] qrDataArray = qrData.split(", ");
            if (qrDataArray.length == 1) {
                etVpa.setText(qrDataArray[0]);
            } else {
                etVpa.setText(qrDataArray[0]);
                etAmount.setText(qrDataArray[1]);
            }
            String externalId = etVpa.getText().toString();
            if (etAmount.getText().toString().isEmpty()) {
                showSnackbar(Constants.PLEASE_ENTER_AMOUNT);
                return;
            }
            double amount = Double.parseDouble(etAmount.getText().toString());
            if (!mTransferPresenter.checkSelfTransfer(externalId)) {
                mTransferPresenter.checkBalanceAvailability(externalId, amount);
            } else {
                showSnackbar(Constants.SELF_ACCOUNT_ERROR);
            }

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
            if (mBtnMobile.isSelected()) {
                showSnackbar(Constants.ERROR_FINDING_MOBILE_NUMBER);
            } else {
                showSnackbar(Constants.ERROR_FINDING_VPA);
            }
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
                return;
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