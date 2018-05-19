package org.mifos.mobilewallet.mifospay.kyc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ankur on 17/May/2018
 */

public class KYCLevel2Fragment extends BaseFragment implements KYCContract.KYCLevel2View {

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

    @Override
    public void setPresenter(KYCContract.KYCLevel2Presenter presenter) {
        mKYCLevel2Presenter = presenter;
    }

    public static KYCLevel2Fragment newInstance() {

        Bundle args = new Bundle();

        KYCLevel2Fragment fragment = new KYCLevel2Fragment();
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_kyc_lvl2, container, false);
        ButterKnife.bind(this, rootView);
        mPresenter.attachView(this);
        setToolbarTitle("KYC Registration Level 2");

        return rootView;
    }

    @OnClick(R.id.btn_browse)
    public void onBrowseClicked() {
        mKYCLevel2Presenter.browseDocs();
    }

    @OnClick(R.id.btn_submit)
    public void onSubmitClicked() {
        mKYCLevel2Presenter.uploadKYCDocs(etIdname.getText().toString());
    }

    @Override
    public void startDocChooseActivity(Intent intent, int READ_REQUEST_CODE) {
        startActivityForResult(Intent.createChooser(intent, "Choose File"), READ_REQUEST_CODE);
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
}
