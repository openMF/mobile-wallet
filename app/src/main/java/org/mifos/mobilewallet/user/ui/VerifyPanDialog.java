package org.mifos.mobilewallet.user.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.auth.ui.BusinessDetailsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by naman on 22/6/17.
 */

public class VerifyPanDialog extends BottomSheetDialogFragment {


    @BindView(R.id.et_pan_number)
    EditText etPanNumber;

    @BindView(R.id.btn_verify)
    Button btnVerify;

    @BindView(R.id.btn_cancel)
    Button btnCancel;

    private BottomSheetBehavior mBehavior;

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.dialog_add_pan, null);

        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());

        ButterKnife.bind(this, view);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPan();
            }
        });

        return dialog;
    }

    private void verifyPan() {
        dismiss();
        if (getActivity() instanceof UserDetailsActivity) {
            ((UserDetailsActivity) getActivity()).verifyPan(etPanNumber.getText().toString());
        } else if (getActivity() instanceof BusinessDetailsActivity) {
            ((BusinessDetailsActivity) getActivity()).verifyPan(etPanNumber.getText().toString());
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }


}
