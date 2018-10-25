package org.mifos.mobilewallet.mifospay.savedcards.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.mifos.mobilewallet.core.data.fineract.entity.savedcards.Card;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.savedcards.CardsContract;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ankur on 19/May/2018
 */

public class AddCardDialog extends BottomSheetDialogFragment {

    boolean forEdit;
    Card editCard;
    @BindView(R.id.et_card_number)
    EditText etCardNumber;
    @BindView(R.id.et_cvv)
    EditText etCVV;
    @BindView(R.id.spn_mm)
    Spinner spnMM;
    @BindView(R.id.spn_yy)
    Spinner spnYY;
    @BindView(R.id.et_fName)
    EditText etFname;
    @BindView(R.id.et_lName)
    EditText etLname;
    @BindView(R.id.btn_add)
    Button btnAdd;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    CardsContract.CardsPresenter mCardsPresenter;
    private BottomSheetBehavior mBottomSheetBehavior;

    public void setCardsPresenter(
            CardsContract.CardsPresenter cardsPresenter) {
        mCardsPresenter = cardsPresenter;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.dialog_add_card, null);

        dialog.setContentView(view);
        mBottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());

        ButterKnife.bind(this, view);

        // add items to spnYY
        List<String> items = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 50; i++) {
            int j = currentYear + i;
            items.add("" + j);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, items);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnYY.setAdapter(dataAdapter);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        // make swipe-able up and parameters to edit text to num only and do backend

        if (forEdit) {
            etFname.setText(editCard.getFirstName());
            etLname.setText(editCard.getLastName());
            etCardNumber.setText(editCard.getCardNumber());
            btnAdd.setText(Constants.UPDATE);
        }
    }

    @OnClick(R.id.btn_cancel)
    public void onCancelClicked() {
        dismiss();
    }

    @OnClick(R.id.btn_add)
    public void onAddClicked() {

        Card card = new Card(etCardNumber.getText().toString(), etCVV.getText().toString(),
                (String) spnMM.getSelectedItem() + "/" + (String) spnYY.getSelectedItem(),
                etFname.getText().toString(), etLname.getText().toString());

        if (forEdit) {
            card.setId(editCard.getId());
            mCardsPresenter.editCard(card);
        } else {
            mCardsPresenter.addCard(card);
        }

        dismiss();
    }
}
