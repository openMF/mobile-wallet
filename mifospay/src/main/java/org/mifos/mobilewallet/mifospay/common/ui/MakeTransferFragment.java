package org.mifos.mobilewallet.mifospay.common.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    @BindView(R.id.ll_make_transfer_container)
    ViewGroup makeTransferContainer;
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

    public static MakeTransferFragment newInstance(String toExternalId, double amount) {

        Bundle args = new Bundle();
        args.putString(Constants.TO_EXTERNAL_ID, toExternalId);
        args.putDouble(Constants.AMOUNT, amount);
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

        mTransferPresenter.fetchClient(getArguments().getString(Constants.TO_EXTERNAL_ID));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTransferPresenter.makeTransfer(localRepository.getClientDetails().getClientId(),
                        toClientId, amount);
                TransitionManager.beginDelayedTransition(makeTransferContainer);
                tvTransferStatus.setText(Constants.SENDING_MONEY);
                progressBar.setVisibility(View.VISIBLE);
                contentView.setVisibility(View.GONE);
            }
        });

        return dialog;
    }

    @Override
    public void showToClientDetails(long clientId, String name, String externalId) {
        this.toClientId = clientId;

        TransitionManager.beginDelayedTransition(makeTransferContainer);
        tvClientName.setText(name);
        tvAmount.setText(Constants.RUPEE + " " + amount);
        tvClientVpa.setText(externalId);

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
        TransitionManager.beginDelayedTransition(makeTransferContainer);
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
        if (getTargetFragment() != null) {
            getTargetFragment().onActivityResult(SendFragment.REQUEST_SHOW_DETAILS,
                    Activity.RESULT_CANCELED, null);
            dismiss();
        }
    }

    @Override
    public void enableDragging(final boolean enable) {
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING && !enable) {
                    mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) { }
        });
    }

}