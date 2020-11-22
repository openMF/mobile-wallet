package org.mifos.mobilewallet.mifospay.kyc.ui;

import android.Manifest;
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

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.kyc.KYCContract;
import org.mifos.mobilewallet.mifospay.kyc.presenter.KYCLevel2Presenter;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ankur on 17/May/2018
 */

public class KYCLevel2Fragment extends BaseFragment implements KYCContract.KYCLevel2View {

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;

    @Inject
    KYCLevel2Presenter mPresenter;

    KYCContract.KYCLevel2Presenter mKYCLevel2Presenter;

    @BindView(R.id.btn_browse)
    Button btnBrowse;

    @BindView(R.id.btn_submit)
    Button btnSubmit;

    @BindView(R.id.tv_filename)
    TextView tvFilename;

    @BindView(R.id.et_idname)
    EditText etIdname;

    public static KYCLevel2Fragment newInstance() {

        Bundle args = new Bundle();

        KYCLevel2Fragment fragment = new KYCLevel2Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setPresenter(KYCContract.KYCLevel2Presenter presenter) {
        mKYCLevel2Presenter = presenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_kyc_lvl2, container, false);
        ButterKnife.bind(this, rootView);
        mPresenter.attachView(this);
        //setToolbarTitle(Constants.KYC_REGISTRATION_LEVEL_2);

        return rootView;
    }

    @OnClick(R.id.btn_browse)
    public void onBrowseClicked() {

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_STORAGE);
        } else {

            // Permission has already been granted
            mKYCLevel2Presenter.browseDocs();
        }
    }

    @OnClick(R.id.btn_submit)
    public void onSubmitClicked() {
        showProgressDialog(getString(R.string.please_wait));
        mKYCLevel2Presenter.uploadKYCDocs(etIdname.getText().toString());
    }

    @Override
    public void startDocChooseActivity(Intent intent, int READ_REQUEST_CODE) {
        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_file)),
                READ_REQUEST_CODE);
    }

    @Override
    public void setFilename(String absolutePath) {
        tvFilename.setText(absolutePath);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mKYCLevel2Presenter.updateFile(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // storage-related task you need to do.

                    mKYCLevel2Presenter.browseDocs();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    showToast(getString(
                            R.string.need_external_storage_permission_to_browse_documents));
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void showToast(String s) {
        Toaster.showToast(getContext(), s);
    }

    @Override
    public void hideProgressDialog() {
        super.hideProgressDialog();
    }

    @Override
    public void goBack() {
        Intent intent = getActivity().getIntent();
        getActivity().finish();
        startActivity(intent);
    }
}
