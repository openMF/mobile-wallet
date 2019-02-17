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
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.common.ui.MakeTransferFragment;
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract;
import org.mifos.mobilewallet.mifospay.home.presenter.TransferPresenter;
import org.mifos.mobilewallet.mifospay.qr.ui.ReadQrActivity;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

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

    @BindView(R.id.chip_send_vpa_transfer)
    Chip mChipVpaTransfer;

    @BindView(R.id.chip_send_mobile_transfer)
    Chip mChipMobileTransfer;

    @BindView(R.id.et_send_amount)
    EditText mEtAmount;

    @BindView(R.id.et_send_transfer_method_address)
    EditText mEtTransferMethodAddress;

    @BindView(R.id.btn_send_enter_transfer_address_externally)
    ImageButton mBtnScanTransferAddress;

    private enum TransferMethod {
        VPA,
        MOBILE
    }

    private TransferMethod transferMethod;

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
        mPresenter.fetchVpa();
        mPresenter.fetchMobile();

        transferMethod = TransferMethod.VPA;
        setupTransferMethodView();

        return rootView;
    }

    private void setupTransferMethodView() {

        int externalSelectionImageDrawableResId;
        int hintStringResId;
        int addressInputType;

        switch (transferMethod) {
            case MOBILE:
                externalSelectionImageDrawableResId = R.drawable.ic_contact;
                hintStringResId = R.string.mobile_number;
                addressInputType = InputType.TYPE_CLASS_NUMBER;
                break;
            case VPA:
            default:
                externalSelectionImageDrawableResId = R.drawable.qrcode_blue_selector;
                hintStringResId = R.string.virtual_payment_address;
                addressInputType = InputType.TYPE_CLASS_TEXT;
        }
        mBtnScanTransferAddress.setBackgroundResource(externalSelectionImageDrawableResId);
        mEtTransferMethodAddress.setInputType(addressInputType);
        mEtTransferMethodAddress.setHint(hintStringResId);
        mEtTransferMethodAddress.setText("");
    }

    @OnClick(R.id.chip_send_vpa_transfer)
    public void onVpaTransferSelected() {
        /* We won't let user un-select the chip
        At least one chip needs to be selected */
        if (transferMethod == TransferMethod.VPA) {
            mChipVpaTransfer.setChecked(true);
        } else {
            transferMethod = TransferMethod.VPA;
            setupTransferMethodView();
        }
    }

    @OnClick(R.id.chip_send_mobile_transfer)
    public void onMobileTransferSelected() {
        /* We won't let user un-select the chip
        At least one chip needs to be selected */
        if (transferMethod == TransferMethod.MOBILE) {
            mChipMobileTransfer.setChecked(true);
        } else {
            transferMethod = TransferMethod.MOBILE;
            setupTransferMethodView();
        }
    }

    @OnClick(R.id.btn_send_proceed)
    public void onTransferClicked() {
        String externalId = mEtTransferMethodAddress.getText().toString().trim();
        String eamount = mEtAmount.getText().toString().trim();
        String mobileNumber = mEtTransferMethodAddress.getText().toString().trim()
                .replaceAll("\\s+", "");
        if (eamount.equals("") || (externalId.equals("") && mobileNumber.equals(""))) {
            Toast.makeText(getActivity(),
                    Constants.PLEASE_ENTER_ALL_THE_FIELDS, Toast.LENGTH_SHORT).show();
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

    @OnClick(R.id.btn_send_enter_transfer_address_externally)
    public void onEnterTransferAddressExternallyClicked() {
        if (transferMethod == TransferMethod.VPA) {
            startBarcodeScan();
        } else {
            startNumberFromContactsSelection();
        }
    }

    private void startBarcodeScan() {
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

    private void startNumberFromContactsSelection() {
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
    public void showVpa(String vpa) {
    }

    @Override
    public void setPresenter(BaseHomeContract.TransferPresenter presenter) {
        this.mTransferPresenter = presenter;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SCAN_QR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String qrData = data.getStringExtra(Constants.QR_DATA);
            mEtTransferMethodAddress.setText(qrData);
            String externalId = mEtTransferMethodAddress.getText().toString();
            double amount = Double.parseDouble(mEtAmount.getText().toString());
            MakeTransferFragment fragment = MakeTransferFragment.newInstance(externalId,
                    amount);
            fragment.show(getChildFragmentManager(),
                    Constants.MAKE_TRANSFER_FRAGMENT);

        } else if (requestCode == PICK_CONTACT && resultCode == Activity.RESULT_OK) {
            Cursor cursor;
            try {
                String phoneNo;
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

                mEtTransferMethodAddress.setText(phoneNo);

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