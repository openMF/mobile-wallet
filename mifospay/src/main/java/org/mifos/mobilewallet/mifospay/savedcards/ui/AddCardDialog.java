package org.mifos.mobilewallet.mifospay.savedcards.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.mifos.mobilewallet.core.data.fineract.entity.savedcards.Card;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.savedcards.CardsContract;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This is a Dialog class to add a new card.
 *
 * @author ankur
 * @since 19/May/2018
 */

public class AddCardDialog extends BottomSheetDialogFragment {

    private static final String TAG = "AddCardDialog";
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
    @BindViews({R.id.til_fName, R.id.til_lName, R.id.til_card_number, R.id.til_cvv})
    List<TextInputLayout> mTextInputLayouts;
    private BottomSheetBehavior mBottomSheetBehavior;
    private boolean fieldsValid;
    private final ButterKnife.Action<TextInputLayout> CHECK_ERROR =
            new ButterKnife.Action<TextInputLayout>() {
                @Override
                public void apply(@NonNull TextInputLayout view, int index) {
                    EditText editText = view.getEditText();
                    if (editText == null || editText.getEditableText() == null
                            || editText.getEditableText().toString().trim().isEmpty()) {
                        view.setError(AddCardDialog.this.getString(R.string.field_required));
                        fieldsValid = false;
                    } else {
                        view.setError(null);
                    }
                }
            };

    /**
     * A function to set the Presenter.
     *
     * @param cardsPresenter : Cards Presenter from Contract Class.
     */
    public void setCardsPresenter(
            CardsContract.CardsPresenter cardsPresenter) {
        mCardsPresenter = cardsPresenter;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @NonNull
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
            String[] expiryDate = editCard.getExpiryDate().split("/");
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            spnMM.setSelection(Integer.parseInt(expiryDate[0]) - 1);
            spnYY.setSelection(Integer.parseInt(expiryDate[1]) - currentYear);
            btnAdd.setText(Constants.UPDATE);
        }
    }

    /**
     * A function to dismiss the dialog box when user presses cancel button.
     */
    @OnClick(R.id.btn_cancel)
    public void onCancelClicked() {
        dismiss();
    }

    /**
     * A function to add a new card after entering all the valid details.
     */
    @OnClick(R.id.btn_add)
    public void onAddClicked() {
        if (!areFieldsValid()) {
            return;
        }
        Card card = new Card(etCardNumber.getText().toString(), etCVV.getText().toString(),
                spnMM.getSelectedItem() + "/" + spnYY.getSelectedItem(),
                etFname.getText().toString(), etLname.getText().toString());

        if (forEdit) {
            card.setId(editCard.getId());
            mCardsPresenter.editCard(card);
        } else {
            mCardsPresenter.addCard(card);
        }

        dismiss();
    }

    /**
     * An utility function to check if the new Card entries are valid or not.
     */
    private boolean areFieldsValid() {
        fieldsValid = true;
        ButterKnife.apply(mTextInputLayouts, CHECK_ERROR);
        int expiryMonth = Integer.parseInt(spnMM.getSelectedItem().toString());
        int expiryYear = Integer.parseInt(spnYY.getSelectedItem().toString());
        Calendar calendar = Calendar.getInstance();
        if (expiryYear == calendar.get(Calendar.YEAR)
                && expiryMonth < (calendar.get(Calendar.MONTH) + 1) && fieldsValid) {
            Toaster.showToast(getContext(), getString(R.string.card_expiry_message));
            fieldsValid = false;
        }
        return fieldsValid;
    }
}
