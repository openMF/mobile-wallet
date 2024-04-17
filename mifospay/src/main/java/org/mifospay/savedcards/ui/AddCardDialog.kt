package org.mifospay.savedcards.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import butterknife.Action
import butterknife.BindView
import butterknife.BindViews
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.ViewCollections
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.mifospay.core.model.entity.savedcards.Card
import org.mifospay.R
import org.mifospay.savedcards.CardsContract
import org.mifospay.common.Constants
import org.mifospay.utils.Toaster
import java.util.Calendar

/**
 * This is a Dialog class to add a new card.
 *
 * @author ankur
 * @since 19/May/2018
 */
class AddCardDialog : BottomSheetDialogFragment() {
    @JvmField
    var forEdit = false

    @JvmField
    var editCard: Card? = null

    @JvmField
    @BindView(R.id.et_card_number)
    var etCardNumber: EditText? = null

    @JvmField
    @BindView(R.id.et_cvv)
    var etCVV: EditText? = null

    @JvmField
    @BindView(R.id.spn_mm)
    var spnMM: Spinner? = null

    @JvmField
    @BindView(R.id.spn_yy)
    var spnYY: Spinner? = null

    @JvmField
    @BindView(R.id.et_fName)
    var etFname: EditText? = null

    @JvmField
    @BindView(R.id.et_lName)
    var etLname: EditText? = null

    @JvmField
    @BindView(R.id.btn_add)
    var btnAdd: Button? = null

    @JvmField
    @BindView(R.id.btn_cancel)
    var btnCancel: Button? = null
    var mCardsPresenter: CardsContract.CardsPresenter? = null

    @BindViews(R.id.til_fName, R.id.til_lName, R.id.til_card_number, R.id.til_cvv)
    lateinit var mTextInputLayouts: MutableList<TextInputLayout>

    private var mBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var fieldsValid = false
    private val checkError: Action<TextInputLayout> = Action { view, index ->
        val editText = view.editText
        if (editText == null || editText.editableText == null || editText.editableText.toString()
                .trim { it <= ' ' }.isEmpty()
        ) {
            view.error = this@AddCardDialog.getString(R.string.field_required)
            fieldsValid = false
        } else {
            view.error = null
        }
    }

    /**
     * A function to set the Presenter.
     *
     * @param cardsPresenter : Cards Presenter from Contract Class.
     */
    fun setCardsPresenter(
        cardsPresenter: CardsContract.CardsPresenter?
    ) {
        mCardsPresenter = cardsPresenter
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view = View.inflate(context, R.layout.dialog_add_card, null)
        dialog.setContentView(view)
        mBottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        ButterKnife.bind(this, view)

        // add items to spnYY
        val items: MutableList<String> = ArrayList()
        val currentYear = Calendar.getInstance()[Calendar.YEAR]
        for (i in 0..49) {
            val j = currentYear + i
            items.add("" + j)
        }
        val dataAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, items
        )
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnYY!!.adapter = dataAdapter
        return dialog
    }

    override fun onStart() {
        super.onStart()
        mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
        // make swipe-able up and parameters to edit text to num only and do backend
        if (forEdit) {
            etFname!!.setText(editCard!!.firstName)
            etLname!!.setText(editCard!!.lastName)
            etCardNumber!!.setText(editCard!!.cardNumber)
            val expiryDate =
                editCard!!.expiryDate.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            val currentYear = Calendar.getInstance()[Calendar.YEAR]
            spnMM!!.setSelection(expiryDate[0].toInt() - 1)
            spnYY!!.setSelection(expiryDate[1].toInt() - currentYear)
            btnAdd!!.text = Constants.UPDATE
        }
    }

    /**
     * A function to dismiss the dialog box when user presses cancel button.
     */
    @OnClick(R.id.btn_cancel)
    fun onCancelClicked() {
        dismiss()
    }

    /**
     * A function to add a new card after entering all the valid details.
     */
    @OnClick(R.id.btn_add)
    fun onAddClicked() {
        if (!areFieldsValid()) {
            return
        }
        val card =
            Card(etCardNumber!!.text.toString()
                .trim { it <= ' ' },
                etCVV!!.text.toString(),
                spnMM!!.selectedItem.toString() + "/" + spnYY!!.selectedItem,
                etFname!!.text.toString().trim { it <= ' ' },
                etLname!!.text.toString().trim { it <= ' ' })
        if (forEdit) {
            card.id = editCard!!.id
            mCardsPresenter!!.editCard(card)
        } else {
            mCardsPresenter!!.addCard(card)
        }
        dismiss()
    }

    /**
     * An utility function to check if the new Card entries are valid or not.
     */
    private fun areFieldsValid(): Boolean {
        fieldsValid = true
        ViewCollections.run(mTextInputLayouts, checkError)
        val expiryMonth = spnMM!!.selectedItem.toString().toInt()
        val expiryYear = spnYY!!.selectedItem.toString().toInt()
        val calendar = Calendar.getInstance()
        if (expiryYear == calendar[Calendar.YEAR] && expiryMonth < calendar[Calendar.MONTH] + 1 && fieldsValid) {
            Toaster.showToast(context, getString(R.string.card_expiry_message))
            fieldsValid = false
        }
        return fieldsValid
    }

    companion object {
        private const val TAG = "AddCardDialog"
    }
}