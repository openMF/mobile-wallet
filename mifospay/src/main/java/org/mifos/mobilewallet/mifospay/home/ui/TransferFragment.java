package org.mifos.mobilewallet.mifospay.home.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.common.ui.MakeTransferFragment;
import org.mifos.mobilewallet.mifospay.home.HomeContract;
import org.mifos.mobilewallet.mifospay.home.presenter.TransferPresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by naman on 30/8/17.
 */

public class TransferFragment extends BaseFragment implements HomeContract.TransferView {

    @Inject
    TransferPresenter mPresenter;

    HomeContract.TransferPresenter mTransferPresenter;

    @BindView(R.id.et_amount)
    EditText etAmount;

    @BindView(R.id.et_vpa)
    EditText etVpa;

    @BindView(R.id.btn_transfer)
    Button btnTransfer;


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
        mPresenter.attachView(this);


        return rootView;
    }

    @OnClick(R.id.btn_transfer)
    public void transferClicked() {
        String externalId = etVpa.getText().toString();
        double amount = Double.parseDouble(etAmount.getText().toString());
        MakeTransferFragment fragment = MakeTransferFragment.newInstance(externalId, amount);
        fragment.show(getChildFragmentManager(), "Make Transfer Fragment");
    }

    @Override
    public void setPresenter(HomeContract.TransferPresenter presenter) {
        this.mTransferPresenter = presenter;
    }
}
