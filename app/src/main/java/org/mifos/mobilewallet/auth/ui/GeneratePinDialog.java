package org.mifos.mobilewallet.auth.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.mifos.mobilewallet.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by naman on 27/6/17.
 */

public class GeneratePinDialog extends BottomSheetDialogFragment {

    @BindView(R.id.btn_verify)
    Button btnVerify;

    @BindView(R.id.btn_cancel)
    Button btnCancel;

    @BindView(R.id.et_pin)
    EditText etPin;

    @BindView(R.id.et_pin_confirm)
    EditText etPinConfirm;

    private BottomSheetBehavior mBehavior;

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.dialog_generate_pin, null);

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
                createPin();
            }
        });


        return dialog;
    }

    private void createPin() {
        if (etPin.getText().toString().equals(etPinConfirm.getText().toString())) {
            ((BankAccountActivity) getActivity()).setupComplete();
        } else {
            Toast.makeText(getActivity(), "PIN does not match", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }


}

