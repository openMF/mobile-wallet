package org.mifos.mobilewallet.mifospay.securitychecks.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Harshad Dabhade on 8/1/2021
 */

public class SecurityChecksActivity extends BaseActivity {

    @BindView(R.id.tv_empty_no_transaction_history_title)
    public TextView tvTransactionsStateTitle;

    @BindView(R.id.tv_empty_no_transaction_history_subtitle)
    TextView tvTransactionsStateSubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_checks);
        ButterKnife.bind(this);

        onRootDetected();
    }

    protected void onRootDetected() {

        Resources res = getResources();
        tvTransactionsStateTitle.setText(res.getString(R.string.rooted_msg_title));
        tvTransactionsStateSubtitle.setText(res.getString((R.string.rooted_msg_subtitle)));
    }

    @OnClick(R.id.root_detected)
    public void onRootDetectedClicked() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}