package org.mifos.mobilewallet.invoice.ui;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.graphics.drawable.ArgbEvaluator;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.mifos.mobilewallet.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by naman on 27/6/17.
 */

public class ScanFPDialog extends BottomSheetDialogFragment {

    @BindView(R.id.btn_verify)
    Button btnVerify;

    @BindView(R.id.btn_cancel)
    Button btnCancel;

    @BindView(R.id.iv_fingerprint)
    ImageView ivFingerprint;

    private AnimatedVectorDrawableCompat showFingerprint;
    private AnimatedVectorDrawableCompat scanFingerprint;
    private AnimatedVectorDrawableCompat fingerprintToTick;
    private AnimatedVectorDrawableCompat fingerprintToCross;
    private int backgroundColor;

    private BottomSheetBehavior mBehavior;

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.dialog_scan_fp, null);

        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());

        ButterKnife.bind(this, view);

        backgroundColor = ContextCompat.getColor(getActivity(), R.color.circle_default);
        showFingerprint = AnimatedVectorDrawableCompat.create(getActivity(),
                R.drawable.show_fingerprint);
        scanFingerprint = AnimatedVectorDrawableCompat.create(getActivity(),
                R.drawable.scan_fingerprint);
        fingerprintToTick = AnimatedVectorDrawableCompat.create(getActivity(),
                R.drawable.fingerprint_to_tick);
        fingerprintToCross = AnimatedVectorDrawableCompat.create(getActivity(),
                R.drawable.fingerprint_to_cross);

        setCircleColor(ContextCompat.getColor(getActivity(), R.color.circle_default));

        ivFingerprint.setImageDrawable(scanFingerprint);
        scanFingerprint.start();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return dialog;
    }

    private void setCircleColor(int to) {

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(),
                backgroundColor, to);
        final GradientDrawable ivDrawable = (GradientDrawable) ivFingerprint.getBackground();

        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                ivDrawable.setColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
        backgroundColor = to;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }


}
