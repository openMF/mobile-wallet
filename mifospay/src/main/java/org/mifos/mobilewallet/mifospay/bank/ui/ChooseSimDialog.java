package org.mifos.mobilewallet.mifospay.bank.ui;

import android.app.Dialog;
import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.mifos.mobilewallet.mifospay.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ankur on 09/July/2018
 */

public class ChooseSimDialog extends BottomSheetDialogFragment {

    @BindView(R.id.tv_sim1)
    TextView mTvSim1;
    @BindView(R.id.tv_sim2)
    TextView mTvSim2;
    @BindView(R.id.btn_confirm)
    Button mBtnConfirm;

    private BottomSheetBehavior mBottomSheetBehavior;
    private int selectedSim = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.dialog_choose_sim_dialog, null);

        dialog.setContentView(view);
        mBottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());

        ButterKnife.bind(this, view);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @OnClick({R.id.tv_sim1, R.id.tv_sim2})
    public void onSimSelected(View view) {
        switch (view.getId()) {
            case R.id.tv_sim1:
                selectedSim = 1;
                mTvSim1.setCompoundDrawablesWithIntrinsicBounds(null,
                        getResources().getDrawable(R.drawable.sim_card_selected), null, null);
                mTvSim2.setCompoundDrawablesWithIntrinsicBounds(null,
                        getResources().getDrawable(R.drawable.sim_card_unselected), null, null);

                break;
            case R.id.tv_sim2:
                selectedSim = 2;
                mTvSim2.setCompoundDrawablesWithIntrinsicBounds(null,
                        getResources().getDrawable(R.drawable.sim_card_selected), null, null);
                mTvSim1.setCompoundDrawablesWithIntrinsicBounds(null,
                        getResources().getDrawable(R.drawable.sim_card_unselected), null, null);
                break;
        }
    }

    @OnClick(R.id.btn_confirm)
    public void onConfirmClicked() {
        dismiss();
        if (getActivity() instanceof LinkBankAccountActivity) {
            ((LinkBankAccountActivity) getActivity()).linkBankAccount(selectedSim);
        }
    }
}
