package org.mifos.mobilewallet.mifospay.kyc.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.kyc.KYCContract;
import org.mifos.mobilewallet.mifospay.kyc.presenter.KYCPresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class KYCActivity extends BaseActivity implements KYCContract.KYCView {
    private static final int READ_REQUEST_CODE = 42;
    @Inject
    KYCPresenter mPresenter;

    KYCContract.KYCPresenter mKYCPresenter;

    @BindView(R.id.btn_browse)
    Button btnBrowse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setContentView(R.layout.activity_kyc);
        ButterKnife.bind(this);
        mPresenter.attachView(this);


    }

    @Override
    public void setPresenter(KYCContract.KYCPresenter presenter) {
        mKYCPresenter = presenter;
    }

    @OnClick(R.id.btn_browse)
    public void onBrowseClicked() {
        mKYCPresenter.browseDocs();
    }


    @Override
    public void startDocChooseActivity(Intent intent) {
        startActivityForResult(Intent.createChooser(intent, "Choose File"), READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                Log.d("qxz uri chosen: ",uri.toString());
                mKYCPresenter.uploadDocs(uri);
            }
        }
    }
}
