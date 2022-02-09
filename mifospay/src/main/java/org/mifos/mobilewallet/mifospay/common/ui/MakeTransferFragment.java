package org.mifos.mobilewallet.mifospay.common.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.mifos.mobilewallet.core.domain.model.PaymentMethod;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.common.TransferContract;
import org.mifos.mobilewallet.mifospay.common.presenter.MakeTransferPresenter;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.payments.ui.SendFragment;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by naman on 30/8/17.
 */

public class MakeTransferFragment extends BottomSheetDialogFragment
        implements TransferContract.TransferView {

    @Inject
    MakeTransferPresenter mPresenter;

    @Inject
    LocalRepository localRepository;

    TransferContract.TransferPresenter mTransferPresenter;

    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    @BindView(R.id.btn_cancel)
    Button btnCancel;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.tv_amount)
    TextView tvAmount;

    @BindView(R.id.tv_client_name)
    TextView tvClientName;

    @BindView(R.id.tv_client_vpa)
    TextView tvClientVpa;

    @BindView(R.id.tv_transfer_status)
    TextView tvTransferStatus;

    @BindView(R.id.ll_content)
    View contentView;

    @BindView(R.id.view_transfer_success)
    View viewTransferSuccess;

    @BindView(R.id.view_transfer_failure)
    View viewTransferFailure;

    private BottomSheetBehavior mBehavior;
    private long toClientId;
    private double amount;
    private String toClientIdentifier;
    private PaymentMethod paymentMethod;

    public static MakeTransferFragment newInstance(
            String toClientIdentifier, PaymentMethod paymentMethod, double amount) {

        Bundle args = new Bundle();
        args.putString(Constants.TO_CLIENT_IDENTIFIER, toClientIdentifier);
        args.putDouble(Constants.AMOUNT, amount);
        args.putSerializable(Constants.PAYMENT_METHOD, paymentMethod);
        MakeTransferFragment fragment = new MakeTransferFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.fragment_make_transfer, null);

        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());

        ButterKnife.bind(this, view);
        mPresenter.attachView(this);

        amount = getArguments().getDouble(Constants.AMOUNT);
        paymentMethod = (PaymentMethod) getArguments().getSerializable(Constants.PAYMENT_METHOD);
        toClientIdentifier = getArguments().getString(Constants.TO_CLIENT_IDENTIFIER);
        if (paymentMethod == PaymentMethod.VPA) {
            mTransferPresenter.fetchClient(toClientIdentifier);
        } else {
            showConfirmMsisdnScreen(toClientIdentifier);
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvTransferStatus.setText(Constants.SENDING_MONEY);
                progressBar.setVisibility(View.VISIBLE);
                contentView.setVisibility(View.GONE);
                if (paymentMethod == PaymentMethod.VPA) {
                    mTransferPresenter.fetchToClientAccount(toClientId, amount);
                } else {
                    mTransferPresenter.makeTransferUsingMsisdn(toClientIdentifier, amount);
                }
            }
        });

        return dialog;
    }

    private void showConfirmMsisdnScreen(String msisdnNumber) {
        tvClientName.setText(msisdnNumber);
        tvAmount.setText(Constants.RUPEE + " " + amount);

        contentView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showToClientDetails(long clientId, String name, String clientIdentifier) {
        this.toClientId = clientId;

        tvClientName.setText(name);
        tvAmount.setText(Constants.RUPEE + " " + amount);
        tvClientVpa.setText(clientIdentifier);

        contentView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void transferSuccess() {
        tvTransferStatus.setText(Constants.TRANSACTION_SUCCESSFUL);
        progressBar.setVisibility(View.GONE);
        viewTransferSuccess.setVisibility(View.VISIBLE);
    }

    @Override
    public void transferFailure() {
        tvTransferStatus.setText(Constants.UNABLE_TO_PROCESS_TRANSFER);
        progressBar.setVisibility(View.GONE);
        viewTransferFailure.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void setPresenter(TransferContract.TransferPresenter presenter) {
        this.mTransferPresenter = presenter;
    }

    @Override
    public void showVpaNotFoundSnackbar() {
        if (getTargetFragment() != null && paymentMethod == PaymentMethod.VPA) {
            getTargetFragment().onActivityResult(SendFragment.REQUEST_SHOW_DETAILS,
                    Activity.RESULT_CANCELED, null);
            dismiss();
        }
    }
}